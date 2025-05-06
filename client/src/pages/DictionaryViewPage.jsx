import "@toast-ui/editor/dist/toastui-editor-viewer.css";
import { Viewer } from "@toast-ui/react-editor";
import { useEffect, useRef, useState } from "react";
import { AiOutlineLoading } from "react-icons/ai";
import { BsBook, BsChevronDown, BsChevronUp, BsEyeSlash } from "react-icons/bs";
import { useNavigate, useParams } from "react-router-dom";
import { formatKoreanDateTime } from "../utils/dateUtils";

const DictionaryViewPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [dictionary, setDictionary] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [toc, setToc] = useState([]);
  const [isTocOpen, setIsTocOpen] = useState(true);
  const viewerRef = useRef(null);

  const scrollToHeading = (text, index) => {
    const headings = document.querySelectorAll(
      ".toastui-editor-viewer h1, .toastui-editor-viewer h2, .toastui-editor-viewer h3, .toastui-editor-viewer h4, .toastui-editor-viewer h5, .toastui-editor-viewer h6"
    );
    const targetHeading = Array.from(headings).find(
      (heading, idx) => heading.textContent.trim() === text && idx === index
    );

    if (targetHeading) {
      targetHeading.scrollIntoView({ behavior: "smooth", block: "start" });
    }
  };

  useEffect(() => {
    const fetchDictionary = async () => {
      setIsLoading(true);

      try {
        const response = await fetch(
          `http://localhost:8080/api/v1/dictionaries/histories/${id}`
        );
        const data = await response.json();
        setDictionary(data);

        // 목차 생성
        if (data.content) {
          const headings = data.content.match(/^#{1,6}\s.+$/gm) || [];
          const tocItems = headings.map((heading, index) => {
            const level = heading.match(/^#+/)[0].length;
            const text = heading.replace(/^#+\s/, "");
            return { level, text, index };
          });
          setToc(tocItems);
        }
      } catch (error) {
        console.error("사전 정보를 가져오는데 실패했습니다:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDictionary();
  }, [id]);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <AiOutlineLoading className="animate-spin text-4xl text-gray-700" />
      </div>
    );
  }

  if (!dictionary) {
    return <div>사전 정보를 찾을 수 없습니다.</div>;
  }

  return (
    <div>
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4">
        <div className="flex items-center gap-2">
          <h2 className="text-xl font-semibold flex items-center gap-2">
            <BsBook className="text-2xl text-gray-700" />
            {dictionary.title}
          </h2>
        </div>
        <div className="flex flex-col sm:flex-row gap-2 mt-2 sm:mt-0">
          <button
            className={`w-full sm:w-auto px-4 py-2 text-sm font-medium rounded-lg transition-colors ${
              dictionary.status === "HIDDEN"
                ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                : "bg-gray-100 text-gray-700 hover:bg-gray-200"
            }`}
            disabled={dictionary.status === "HIDDEN"}
            onClick={() =>
              navigate(`/dictionary/${dictionary.dictionaryId}/log`)
            }
          >
            편집로그
          </button>
          <button
            className={`w-full sm:w-auto px-4 py-2 text-sm font-medium rounded-lg transition-colors ${
              dictionary.status === "ALL_ACTIVE"
                ? "bg-gray-700 text-white hover:bg-gray-800"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
            disabled={dictionary.status !== "ALL_ACTIVE"}
            onClick={() => navigate(`/dictionary/${id}/edit`)}
          >
            편집하기
          </button>
        </div>
      </div>
      {toc.length > 0 && (
        <div className="mb-6">
          <button
            onClick={() => setIsTocOpen(!isTocOpen)}
            className="text-gray-600 hover:text-gray-900 mb-2 flex items-center gap-1"
          >
            {isTocOpen ? (
              <>
                <span>목차</span>
                <BsChevronUp className="text-lg" />
              </>
            ) : (
              <>
                <span>목차</span>
                <BsChevronDown className="text-lg" />
              </>
            )}
          </button>
          {isTocOpen && (
            <div className="p-4 bg-gray-50 rounded-lg border border-gray-200">
              <ul className="space-y-1">
                {toc.map((item, index) => (
                  <li
                    key={index}
                    className="text-gray-600 hover:text-gray-900 cursor-pointer"
                    style={{ marginLeft: `${(item.level - 1) * 1}rem` }}
                    onClick={() => scrollToHeading(item.text, item.index)}
                  >
                    {item.text}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
      <div>
        {dictionary.content === null ? (
          <div className="flex flex-col items-center justify-center py-8 px-4 bg-gray-50 rounded-lg border border-gray-200">
            <BsEyeSlash className="text-4xl text-gray-400 mb-3" />
            <div className="text-gray-500 font-medium">
              이 문서는 현재 숨김 상태입니다.
            </div>
          </div>
        ) : (
          <div className="toastui-editor-viewer" ref={viewerRef}>
            <Viewer initialValue={dictionary.content} />
          </div>
        )}
      </div>
      <div className="mt-4 text-sm text-gray-500 bg-gray-50 p-4 rounded-lg">
        <p className="flex items-center gap-2">
          <span className="font-medium">마지막 편집:</span>
          <span>{formatKoreanDateTime(dictionary.createdAt)}</span>
        </p>
      </div>
    </div>
  );
};

export default DictionaryViewPage;
