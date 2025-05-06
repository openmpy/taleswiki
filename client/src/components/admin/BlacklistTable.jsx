import React from "react";
import { formatKoreanDateTime } from "../../utils/dateUtils";

function BlacklistTable({ blacklists, onDelete }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              ID
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              IP
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              차단 사유
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              차단 일시
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              관리
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {blacklists.map((blacklist) => (
            <tr
              key={blacklist.blacklistId}
              className="hover:bg-gray-50 transition-colors"
            >
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900">
                {blacklist.blacklistId}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {blacklist.ip}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {blacklist.reason}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {formatKoreanDateTime(blacklist.createdAt)}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-center">
                <button
                  onClick={() => onDelete(blacklist.blacklistId)}
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

export default BlacklistTable;
