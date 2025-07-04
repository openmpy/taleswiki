import React, { useState } from "react";
import { formatKoreanDateTime } from "../../utils/dateUtils";

function BoardTable({ boards, onDelete }) {
  const [copiedIp, setCopiedIp] = useState(null);

  const handleCopyIp = async (ip) => {
    try {
      await navigator.clipboard.writeText(ip);
      setCopiedIp(ip);
      setTimeout(() => setCopiedIp(null), 1000);
    } catch {
      alert("클립보드 복사에 실패했습니다.");
    }
  };

  const handleRowClick = (boardId) => {
    window.open(`/board/${boardId}`, "_blank", "noopener,noreferrer");
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              ID
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider max-w-[200px] overflow-hidden text-ellipsis whitespace-nowrap">
              제목
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              작성자
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              IP
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              작성 시간
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              관리
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {boards.map((board) => (
            <tr
              key={board.boardId}
              className="hover:bg-gray-50 transition-colors cursor-pointer"
              onClick={() => handleRowClick(board.boardId)}
            >
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900">
                {board.boardId}
              </td>
              <td
                className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center max-w-[200px] overflow-hidden text-ellipsis"
                style={{ maxWidth: "200px" }}
              >
                <span
                  className="max-w-[200px] overflow-hidden text-ellipsis inline-block align-middle"
                  style={{ maxWidth: "200px" }}
                >
                  {board.title}
                </span>
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {board.author}
              </td>
              <td
                className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center cursor-pointer relative"
                onClick={(e) => {
                  e.stopPropagation();
                  handleCopyIp(board.ip);
                }}
                title="클릭 시 복사"
              >
                {board.ip}
                {copiedIp === board.ip && (
                  <span className="absolute left-1/2 -translate-x-1/2 top-full mt-1 px-2 py-0.5 bg-gray-700 text-white text-xs rounded shadow z-10 whitespace-nowrap">
                    복사됨!
                  </span>
                )}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {formatKoreanDateTime(board.createdAt)}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-center">
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    onDelete(board.boardId);
                  }}
                  className="text-xs px-2 py-1 bg-red-100 text-red-800 rounded hover:bg-red-200 transition-colors"
                >
                  삭제
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default BoardTable;
