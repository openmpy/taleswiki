import React, { useEffect, useState } from "react";
import { BsClockHistory } from "react-icons/bs";
import { useNavigate, useParams } from "react-router-dom";
import LoadingSpinner from "../components/LoadingSpinner";
import SEO from "../components/SEO";
import axiosInstance from "../utils/axiosConfig";
import { formatKoreanDateTime } from "../utils/dateUtils";

const DictionaryLogPage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [dictionary, setDictionary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDictionaryHistory = async () => {
      try {
        const { data } = await axiosInstance.get(
          `/api/v1/dictionaries/${id}/history`
        );
        setDictionary(data);
      } catch (error) {
        console.error("문서 편집 로그를 불러오는데 실패했습니다:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDictionaryHistory();
  }, [id]);

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!dictionary) {
    return (
      <main className="flex justify-center items-center min-h-[400px]">
        <div className="text-gray-500 font-medium">
          문서 편집 로그 정보를 찾을 수 없습니다.
        </div>
      </main>
    );
  }

  return (
    <main>
      <SEO
        title={`${dictionary.title}`}
        description={`${dictionary.title} 문서의 편집 이력을 확인하세요. 문서의 변경 사항과 편집자를 확인할 수 있습니다.`}
      />
      <header className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4">
        <div className="flex items-center gap-2">
          <h1 className="text-xl font-semibold flex items-center gap-2">
            <BsClockHistory
              className="text-2xl text-gray-700"
              aria-hidden="true"
            />
            편집로그
          </h1>
        </div>
        <nav className="flex flex-col sm:flex-row gap-2 mt-2 sm:mt-0">
          <button
            className={`w-full sm:w-auto px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-gray-100 text-gray-700 hover:bg-gray-200`}
            onClick={() => navigate(-1)}
            aria-label="이전 페이지로 돌아가기"
          >
            뒤로가기
          </button>
        </nav>
      </header>

      <section>
        <h2 className="text-lg font-semibold mb-4">{dictionary?.title}</h2>
        <div className="overflow-x-auto rounded-lg border border-gray-200">
          <table className="min-w-full border-collapse" role="grid">
            <thead className="bg-gray-100">
              <tr>
                <th
                  scope="col"
                  className="px-3 sm:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap"
                >
                  버전
                </th>
                <th
                  scope="col"
                  className="px-3 sm:px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap"
                >
                  생성일자
                </th>
                <th
                  scope="col"
                  className="px-3 sm:px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap"
                >
                  문서크기
                </th>
                <th
                  scope="col"
                  className="px-3 sm:px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap"
                >
                  편집자
                </th>
                <th
                  scope="col"
                  className="px-3 sm:px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap"
                >
                  상태
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {dictionary?.histories.map((history) => (
                <tr
                  key={history.dictionaryHistoryId}
                  className="hover:bg-gray-50 cursor-pointer"
                  onClick={() =>
                    navigate(`/dictionary/${history.dictionaryHistoryId}`)
                  }
                  role="button"
                  tabIndex={0}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                      navigate(`/dictionary/${history.dictionaryHistoryId}`);
                    }
                  }}
                >
                  <td className="px-3 sm:px-6 py-4 text-sm font-medium text-gray-900 whitespace-nowrap">
                    {history.version}
                  </td>
                  <td className="px-3 sm:px-6 py-4 text-sm text-gray-500 text-center whitespace-nowrap">
                    {formatKoreanDateTime(history.createdAt)}
                  </td>
                  <td className="px-3 sm:px-6 py-4 text-sm text-gray-500 text-center whitespace-nowrap">
                    {(history.size / 1024).toFixed(2)}KB
                  </td>
                  <td className="px-3 sm:px-6 py-4 text-sm text-gray-500 text-center whitespace-nowrap">
                    {history.author}
                  </td>
                  <td className="px-3 sm:px-6 py-4 text-center whitespace-nowrap">
                    <span
                      className={`inline-flex items-center px-2.5 py-0.5 rounded text-xs font-medium
                      ${
                        history.dictionaryHistoryStatus === "ALL_ACTIVE"
                          ? "bg-green-100 text-green-800"
                          : history.dictionaryHistoryStatus === "READ_ONLY"
                          ? "bg-orange-100 text-orange-800"
                          : history.dictionaryHistoryStatus === "HIDDEN"
                          ? "bg-gray-100 text-gray-800"
                          : "bg-blue-100 text-blue-800"
                      }`}
                    >
                      {history.dictionaryHistoryStatus === "ALL_ACTIVE"
                        ? "활성"
                        : history.dictionaryHistoryStatus === "READ_ONLY"
                        ? "읽기"
                        : history.dictionaryHistoryStatus === "HIDDEN"
                        ? "숨김"
                        : history.dictionaryHistoryStatus}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  );
};

export default DictionaryLogPage;
