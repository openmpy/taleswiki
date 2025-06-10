import "@toast-ui/editor/dist/toastui-editor-viewer.css";
import { useEffect, useState } from "react";
import ReactDiffViewer from "react-diff-viewer";
import { BsFileCheck } from "react-icons/bs";
import { useNavigate, useParams } from "react-router-dom";
import LoadingSpinner from "../components/LoadingSpinner";
import axiosInstance from "../utils/axiosConfig";

const DictionaryComparePage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [leftVersion, setLeftVersion] = useState(null);
  const [rightVersion, setRightVersion] = useState(null);
  const [versions, setVersions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [versionContents, setVersionContents] = useState({});
  const [dictionary, setDictionary] = useState(null);

  useEffect(() => {
    const fetchDictionary = async () => {
      try {
        const response = await axiosInstance.get(
          `/api/v1/dictionaries/${id}/history`
        );
        setDictionary(response.data);
        setVersions(response.data.histories);

        if (response.data.histories.length >= 2) {
          setLeftVersion(response.data.histories[1].version);
          setRightVersion(response.data.histories[0].version);
        }
        setLoading(false);
      } catch (error) {
        console.error("사전 정보를 불러오는데 실패했습니다:", error);
        setError("사전 정보를 불러오는데 실패했습니다.");
        setLoading(false);
      }
    };

    fetchDictionary();
  }, [id]);

  useEffect(() => {
    const fetchVersionContents = async () => {
      if (!leftVersion || !rightVersion) return;

      try {
        const [leftResponse, rightResponse] = await Promise.all([
          axiosInstance.get(
            `api/v1/dictionaries/${id}/versions/${leftVersion}`
          ),
          axiosInstance.get(
            `api/v1/dictionaries/${id}/versions/${rightVersion}`
          ),
        ]);

        setVersionContents({
          [leftVersion]: leftResponse.data.content,
          [rightVersion]: rightResponse.data.content,
        });
      } catch {
        setError("버전 내용을 불러오는데 실패했습니다.");
      }
    };

    fetchVersionContents();
  }, [id, leftVersion, rightVersion]);

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (versions.length < 2) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[50vh] gap-4">
        <p className="text-lg text-gray-700">
          비교할 버전이 충분하지 않습니다.
        </p>
        <button
          className="px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-gray-100 text-gray-700 hover:bg-gray-200"
          onClick={() => navigate(-1)}
          aria-label="이전 페이지로 돌아가기"
        >
          뒤로가기
        </button>
      </div>
    );
  }

  return (
    <main>
      <header className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4">
        <div className="flex items-center gap-2">
          <h1 className="text-xl font-semibold flex items-center gap-2">
            <BsFileCheck
              className="text-2xl text-gray-700"
              aria-hidden="true"
            />
            버전비교
          </h1>
        </div>
        <nav className="flex flex-col sm:flex-row gap-2 mt-2 sm:mt-0">
          <button
            className="w-full sm:w-auto px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-gray-100 text-gray-700 hover:bg-gray-200"
            onClick={() => navigate(-1)}
            aria-label="이전 페이지로 돌아가기"
          >
            뒤로가기
          </button>
        </nav>
      </header>

      <section>
        <h2 className="text-lg font-semibold mb-4">{dictionary?.title}</h2>

        <div className="mb-4">
          <div className="flex justify-between items-center gap-4">
            <select
              value={leftVersion}
              onChange={(e) => setLeftVersion(Number(e.target.value))}
              className="px-3 py-1.5 border border-gray-300 rounded-lg text-sm bg-white hover:border-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-300 focus:border-transparent transition-colors"
            >
              {versions.map((version) => (
                <option key={version.version} value={version.version}>
                  {`버전 ${version.version}`}
                </option>
              ))}
            </select>
            <select
              value={rightVersion}
              onChange={(e) => setRightVersion(Number(e.target.value))}
              className="px-3 py-1.5 border border-gray-300 rounded-lg text-sm bg-white hover:border-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-300 focus:border-transparent transition-colors"
            >
              {versions.map((version) => (
                <option key={version.version} value={version.version}>
                  {`버전 ${version.version}`}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="border border-gray-200 rounded-lg overflow-hidden">
          <div className="overflow-x-auto">
            <div className="min-w-[800px]">
              <ReactDiffViewer
                oldValue={versionContents[leftVersion] || ""}
                newValue={versionContents[rightVersion] || ""}
                splitView={true}
                showDiffOnly={false}
                leftTitle={`버전 ${leftVersion}`}
                rightTitle={`버전 ${rightVersion}`}
                styles={{
                  variables: {
                    light: {
                      diffViewerBackground: "#fff",
                      diffViewerTitleBackground: "#f8fafc",
                      addedBackground: "#f0fdf4",
                      addedColor: "#166534",
                      removedBackground: "#fef2f2",
                      removedColor: "#991b1b",
                      wordAddedBackground: "#dcfce7",
                      wordRemovedBackground: "#fee2e2",
                      codeFoldBackground: "#f8fafc",
                      codeFoldGutterBackground: "#f1f5f9",
                      codeFoldContentColor: "#64748b",
                      gutterBackground: "#f8fafc",
                      gutterBackgroundDark: "#f1f5f9",
                      highlightBackground: "#f1f5f9",
                      highlightGutterBackground: "#e2e8f0",
                      codeBackground: "#f8fafc",
                      codeGutterBackground: "#f1f5f9",
                      codeGutterBorder: "#e2e8f0",
                      codeGutterColor: "#64748b",
                      codeFontFamily:
                        "ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace",
                      codeFontSize: "14px",
                      codeLineHeight: "1.5",
                      codePadding: "0.5rem",
                      codeGutterPadding: "0.5rem",
                      codeGutterWidth: "3rem",
                      codeGutterBorderWidth: "1px",
                      codeGutterBorderColor: "#e2e8f0",
                      codeGutterBorderStyle: "solid",
                      codeGutterBorderRadius: "0.375rem",
                      codeGutterMargin: "0.5rem",
                      codeGutterBackgroundDark: "#f1f5f9",
                      codeGutterBackgroundLight: "#ffffff",
                      codeGutterBackgroundHover: "#f1f5f9",
                      codeGutterBackgroundHoverDark: "#e2e8f0",
                      codeGutterBackgroundHoverLight: "#f8fafc",
                      codeGutterBackgroundActive: "#e2e8f0",
                      codeGutterBackgroundActiveDark: "#cbd5e1",
                      codeGutterBackgroundActiveLight: "#f1f5f9",
                      codeGutterBackgroundSelected: "#e2e8f0",
                      codeGutterBackgroundSelectedDark: "#cbd5e1",
                      codeGutterBackgroundSelectedLight: "#f1f5f9",
                      codeGutterBackgroundSelectedHover: "#cbd5e1",
                      codeGutterBackgroundSelectedHoverDark: "#94a3b8",
                      codeGutterBackgroundSelectedHoverLight: "#e2e8f0",
                      codeGutterBackgroundSelectedActive: "#94a3b8",
                      codeGutterBackgroundSelectedActiveDark: "#64748b",
                      codeGutterBackgroundSelectedActiveLight: "#cbd5e1",
                    },
                  },
                  titleBlock: {
                    padding: "0.75rem 1rem",
                    borderBottom: "1px solid #e2e8f0",
                    fontSize: "0.875rem",
                    fontWeight: "500",
                    color: "#1e293b",
                  },
                  splitView: {
                    padding: "1rem",
                    width: "100%",
                  },
                  contentText: {
                    fontSize: "0.875rem",
                    lineHeight: "1.5",
                    fontFamily:
                      "ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace",
                    whiteSpace: "pre-wrap",
                    wordBreak: "break-word",
                    width: "100%",
                  },
                }}
              />
            </div>
          </div>
        </div>
      </section>
    </main>
  );
};

export default DictionaryComparePage;
