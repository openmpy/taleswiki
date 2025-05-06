import axios from "axios";
import React, { useEffect, useState } from "react";
import { AiOutlineLoading } from "react-icons/ai";
import { BsClockHistory } from "react-icons/bs";
import { useNavigate, useParams } from "react-router-dom";
import { formatKoreanDateTime } from "../utils/dateUtils";

const DictionaryLogPage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [dictionary, setDictionary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDictionaryHistory = async () => {
      try {
        const { data } = await axios.get(
          `http://localhost:8080/api/v1/dictionaries/${id}/history`
        );
        setDictionary(data);
      } catch (error) {
        console.error("사전 편집 로그를 불러오는데 실패했습니다:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDictionaryHistory();
  }, [id]);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <AiOutlineLoading className="animate-spin text-4xl text-gray-700" />
      </div>
    );
  }

  if (!dictionary) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="text-gray-500 font-medium">
          사전 편집 로그 정보를 찾을 수 없습니다.
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4">
        <div className="flex items-center gap-2">
          <h2 className="text-xl font-semibold flex items-center gap-2">
            <BsClockHistory className="text-2xl text-gray-700" />
            편집로그
          </h2>
        </div>
        <div className="flex flex-col sm:flex-row gap-2 mt-2 sm:mt-0">
          <button
            className={`w-full sm:w-auto px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-gray-100 text-gray-700 hover:bg-gray-200`}
            onClick={() => navigate(-1)}
          >
            뒤로가기
          </button>
        </div>
      </div>

      <div>
        <h3 className="text-lg font-semibold mb-4">{dictionary?.title}</h3>
        <div className="overflow-x-auto rounded-lg border border-gray-200">
          <table className="min-w-full border-collapse">
            <thead className="bg-gray-100">
              <tr>
                <th className="px-3 sm:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  버전
                </th>
                <th className="px-3 sm:px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  생성일자
                </th>
                <th className="px-3 sm:px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  문서크기
                </th>
                <th className="px-3 sm:px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  편집자
                </th>
                <th className="px-3 sm:px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
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
                >
                  <td className="px-3 sm:px-6 py-4 text-sm font-medium text-gray-900 whitespace-nowrap">
                    {history.version}
                  </td>
                  <td className="px-3 sm:px-6 py-4 text-sm text-gray-500 text-center whitespace-nowrap">
                    {formatKoreanDateTime(history.createdAt)}
                  </td>
                  <td className="px-3 sm:px-6 py-4 text-sm text-gray-500 text-center whitespace-nowrap">
                    {history.size}KB
                  </td>
                  <td className="px-3 sm:px-6 py-4 text-sm text-gray-500 text-center whitespace-nowrap">
                    {history.author}
                  </td>
                  <td className="px-3 sm:px-6 py-4 text-center whitespace-nowrap">
                    <span
                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
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
      </div>
    </div>
  );
};

export default DictionaryLogPage;
