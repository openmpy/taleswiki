import axios from "axios";
import React, { useEffect, useState } from "react";
import { HiOutlineClock } from "react-icons/hi";
import { formatRelativeTime } from "../utils/dateUtils";

function Sidebar({ className }) {
  const [recentEdits, setRecentEdits] = useState([]);

  useEffect(() => {
    const fetchRecentEdits = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/dictionaries/latest-modified"
        );
        const formattedData = response.data.dictionaries.map((item) => ({
          category: item.category,
          title: item.title,
          date: item.createdAt,
        }));

        setRecentEdits(formattedData);
      } catch (error) {
        console.error("최근 편집 데이터를 가져오는데 실패했습니다:", error);
      }
    };

    fetchRecentEdits();
  }, []);

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
              <span className="text-sm text-gray-800 truncate">
                {item.title}
              </span>
            </div>
          </li>
        ))}
      </ul>
    </aside>
  );
}

export default Sidebar;
