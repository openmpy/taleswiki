import React from "react";
import { formatKoreanDateTime } from "../../utils/dateUtils";

function ChatTable({ chats, onDelete }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-[80px]">
              ID
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-[120px]">
              발신자 IP
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-[120px]">
              닉네임
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              내용
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-[180px]">
              작성 시간
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-[100px]">
              관리
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {chats.map((chat) => (
            <tr
              key={chat.chatId}
              className="hover:bg-gray-50 transition-colors"
            >
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900">
                {chat.chatId}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {chat.sender}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {chat.nickname}
              </td>
              <td className="px-4 py-2 text-sm text-gray-900 text-center">
                <div className="max-w-[400px] max-h-[100px] overflow-y-auto whitespace-pre-wrap break-words">
                  {chat.content}
                </div>
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {formatKoreanDateTime(chat.createdAt)}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-center">
                <button
                  onClick={() => onDelete(chat.chatId)}
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

export default ChatTable;
