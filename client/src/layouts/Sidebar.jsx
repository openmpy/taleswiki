import { useQuery } from "@tanstack/react-query";
import React from "react";
import { HiOutlineClock } from "react-icons/hi";
import { Link } from "react-router-dom";
import axiosInstance from "../utils/axiosConfig";
import { formatRelativeTime } from "../utils/dateUtils";

function Sidebar({ className }) {
  const { data: recentEdits = [] } = useQuery({
    queryKey: ["recentEdits"],
    queryFn: async () => {
      const response = await axiosInstance.get(
        "/api/v1/dictionaries/latest-modified"
      );
      return response.data.dictionaries.map((item) => ({
        currentHistoryId: item.currentHistoryId,
        category: item.category,
        title: item.title,
        date: item.createdAt,
      }));
    },
    staleTime: 5 * 60 * 1000, // 5분 동안 캐시 유지
    cacheTime: 30 * 60 * 1000, // 30분 동안 캐시 데이터 보관
    refetchInterval: 30 * 1000, // 30초마다 데이터 갱신
  });

  return (
    <aside
      className={`${className} bg-white rounded-lg border border-gray-200`}
    >
      <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
        <HiOutlineClock className="text-gray-600" />
        최근 편집
      </h3>
      <ul className="space-y-2">
        {recentEdits.map((item, index) => (
          <li
            key={index}
            className="py-2 border-b border-gray-200 last:border-b-0"
          >
            <Link
              to={`/dictionary/${item.currentHistoryId}`}
              className="block group"
            >
              <div className="flex items-center gap-2">
                <span
                  className={`text-xs px-2 py-0.5 rounded whitespace-nowrap ${
                    item.category === "런너"
                      ? "bg-blue-100 text-blue-800"
                      : "bg-purple-100 text-purple-800"
                  }`}
                >
                  {item.category}
                </span>
                <span className="text-xs text-gray-500 whitespace-nowrap">
                  {formatRelativeTime(item.date)}
                </span>
                <span className="text-sm text-gray-800 truncate group-hover:underline">
                  {item.title}
                </span>
              </div>
            </Link>
          </li>
        ))}
      </ul>
    </aside>
  );
}

export default Sidebar;
