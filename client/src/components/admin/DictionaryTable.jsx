function DictionaryTable({ dictionaries, onStatusChange, onDelete }) {
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
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              상태 수정
            </th>
            <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              삭제
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {dictionaries.map((dictionary) => (
            <tr key={dictionary.dictionaryId}>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900">
                {dictionary.dictionaryId}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-center">
                {dictionary.title}
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-center min-w-[120px]">
                <span
                  className={`px-2 py-0.5 text-xs rounded ${
                    dictionary.category === "런너"
                      ? "bg-blue-100 text-blue-800"
                      : "bg-purple-100 text-purple-800"
                  }`}
                >
                  {dictionary.category}
                </span>
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-center">
                <span
                  className={`px-2 py-0.5 text-xs rounded ${
                    dictionary.status === "ALL_ACTIVE"
                      ? "bg-green-100 text-green-800"
                      : dictionary.status === "READ_ONLY"
                      ? "bg-orange-100 text-orange-800"
                      : "bg-gray-100 text-gray-800"
                  }`}
                >
                  {dictionary.status === "ALL_ACTIVE"
                    ? "활성"
                    : dictionary.status === "READ_ONLY"
                    ? "읽기"
                    : "숨김"}
                </span>
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-center">
                <div className="flex gap-1 justify-center">
                  <button
                    onClick={() =>
                      onStatusChange(dictionary.dictionaryId, "all_active")
                    }
                    className="text-xs px-1 py-0.5 bg-green-100 text-green-800 rounded hover:bg-green-200"
                  >
                    활성
                  </button>
                  <button
                    onClick={() =>
                      onStatusChange(dictionary.dictionaryId, "read_only")
                    }
                    className="text-xs px-1 py-0.5 bg-orange-100 text-orange-800 rounded hover:bg-orange-200"
                  >
                    읽기
                  </button>
                  <button
                    onClick={() =>
                      onStatusChange(dictionary.dictionaryId, "hidden")
                    }
                    className="text-xs px-1 py-0.5 bg-gray-100 text-gray-800 rounded hover:bg-gray-200"
                  >
                    숨김
                  </button>
                </div>
              </td>
              <td className="px-4 py-2 whitespace-nowrap text-center">
                <button
                  onClick={() => onDelete(dictionary.dictionaryId)}
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

export default DictionaryTable;
