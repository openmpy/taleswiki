import { useQuery } from "@tanstack/react-query";
import React from "react";
import { HiOutlineClock, HiOutlineFire } from "react-icons/hi";
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
    staleTime: 5 * 60 * 1000,
    cacheTime: 30 * 60 * 1000,
    refetchInterval: 30 * 1000,
  });

  const { data: popularDocs = [] } = useQuery({
    queryKey: ["popularDocs"],
    queryFn: async () => {
      const response = await axiosInstance.get("/api/v1/dictionaries/popular");
      return response.data.dictionaries.map((item) => ({
        currentHistoryId: item.currentHistoryId,
        category: item.category,
        title: item.title,
      }));
    },
    staleTime: 5 * 60 * 1000,
    cacheTime: 30 * 60 * 1000,
    refetchInterval: 30 * 1000,
  });

  return (
    <div className={`${className} space-y-4`}>
      {/* 인기 문서 섹션 */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <h3 className="text-lg font-semibold mb-4 flex items-center gap-2 text-gray-700">
          <HiOutlineFire className="text-gray-600" />
          인기 문서
        </h3>
        <ul className="space-y-2">
          {popularDocs.length === 0 ? (
            <li className="py-2 text-sm text-gray-500 text-center">
              갱신중입니다.
            </li>
          ) : (
            popularDocs.map((item, index) => (
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
                      className={`flex items-center justify-center w-6 h-6 rounded text-xs font-bold ${
                        index === 0
                          ? "bg-yellow-100 text-yellow-800 border border-yellow-200"
                          : index === 1
                          ? "bg-gray-100 text-gray-800 border border-gray-200"
                          : index === 2
                          ? "bg-orange-100 text-orange-800 border border-orange-200"
                          : "bg-gray-50 text-gray-500 border border-gray-100"
                      }`}
                    >
                      {index + 1}
                    </span>
                    <span
                      className={`text-xs px-2 py-0.5 rounded whitespace-nowrap ${
                        item.category === "런너"
                          ? "bg-blue-100 text-blue-800"
                          : "bg-purple-100 text-purple-800"
                      }`}
                    >
                      {item.category}
                    </span>
                    <span className="text-sm text-gray-800 truncate group-hover:underline">
                      {item.title}
                    </span>
                  </div>
                </Link>
              </li>
            ))
          )}
        </ul>
      </div>

      {/* 최근 편집 섹션 */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <h3 className="text-lg font-semibold mb-4 flex items-center gap-2 text-gray-700">
          <HiOutlineClock className="text-gray-600" />
          최근 편집
        </h3>
        <ul className="space-y-2">
          {recentEdits.length === 0 ? (
            <li className="py-2 text-sm text-gray-500 text-center">
              갱신중입니다.
            </li>
          ) : (
            recentEdits.map((item, index) => (
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
            ))
          )}
        </ul>
      </div>
    </div>
  );
}

export default Sidebar;
