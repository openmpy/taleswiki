import React from "react";
import { useNavigate } from "react-router-dom";

function DictionaryHistoryTable({ histories }) {
  const navigate = useNavigate();

  const handleRowClick = (dictionaryHistoryId) => {
    navigate(`/dictionary/${dictionaryHistoryId}`);
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              ID
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              제목
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              카테고리
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              상태
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {histories.map((history) => (
            <tr
              key={history.dictionaryHistoryId}
              onClick={() => handleRowClick(history.dictionaryHistoryId)}
              className="cursor-pointer hover:bg-gray-50 transition-colors"
            >
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900">
                {history.dictionaryHistoryId}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {history.title}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-center min-w-[120px]">
                <span
                  className={`px-2 py-0.5 text-xs rounded ${
                    history.category === "런너"
                      ? "bg-blue-100 text-blue-800"
                      : "bg-purple-100 text-purple-800"
                  }`}
                >
                  {history.category}
                </span>
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-center">
                <span
                  className={`px-2 py-0.5 text-xs rounded ${
                    history.dictionaryHistoryStatus === "ALL_ACTIVE"
                      ? "bg-green-100 text-green-800"
                      : history.dictionaryHistoryStatus === "READ_ONLY"
                      ? "bg-orange-100 text-orange-800"
                      : "bg-gray-100 text-gray-800"
                  }`}
                >
                  {history.dictionaryHistoryStatus === "ALL_ACTIVE"
                    ? "활성"
                    : history.dictionaryHistoryStatus === "READ_ONLY"
                    ? "읽기"
                    : "숨김"}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default DictionaryHistoryTable;
