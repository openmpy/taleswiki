import React, { useEffect, useState } from "react";
import { BiBook } from "react-icons/bi";
import { Link, useNavigate } from "react-router-dom";
import {
  classifyByInitial,
  createInitialGroups,
  englishInitials,
  getStatusInfo,
  koreanInitials,
} from "../utils/dictionaryUtils";

function DictionaryCategoryPage({ type }) {
  const isRunnerDictionary = type === "runner";
  const [dictionaryGroups, setDictionaryGroups] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDictionaries = async () => {
      try {
        const category = isRunnerDictionary ? "person" : "guild";
        const response = await fetch(
          `http://localhost:8080/api/v1/dictionaries/categories/${category}`
        );
        const data = await response.json();

        // 초기 그룹 구조 생성
        const initialGroups = createInitialGroups();

        // API 데이터를 그룹에 분류
        data.groups.forEach((group) => {
          group.dictionaries.forEach((dict) => {
            const initial = classifyByInitial(dict.title);
            const targetGroup = initialGroups.find(
              (g) => g.initial === initial
            );
            if (targetGroup) {
              targetGroup.dictionaries.push(dict);
            }
          });
        });

        setDictionaryGroups(initialGroups);
      } catch (error) {
        console.error("사전 데이터를 불러오는데 실패했습니다:", error);
      }
    };

    fetchDictionaries();
  }, [isRunnerDictionary]);

  return (
    <>
      <div className="flex justify-between items-center mb-4">
        <div className="flex items-center gap-2">
          <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
            <BiBook className="text-2xl text-gray-700" />
            {isRunnerDictionary ? "런너 사전" : "길드 사전"}
          </h2>
        </div>
        <button
          onClick={() =>
            navigate(
              `/dictionary/write?type=${
                isRunnerDictionary ? "runner" : "guild"
              }`
            )
          }
          className="px-4 py-2 text-sm font-medium bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors shadow-sm"
        >
          작성하기
        </button>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
        {/* 한글 그룹 */}
        {dictionaryGroups.slice(0, koreanInitials.length).map((group) => (
          <div
            key={group.initial}
            className="border border-gray-200 rounded-lg p-4 bg-white"
          >
            <h3 className="text-sm font-bold mb-1 border-b pb-0.5 bg-gray-700 text-white -mx-4 -mt-4 px-4 pt-1.5 rounded-t-lg">
              {group.initial}
            </h3>
            <ul className="space-y-0.5">
              {group.dictionaries.map((dict) => {
                const statusInfo = getStatusInfo(dict.status);
                return (
                  <li
                    key={dict.currentHistoryId}
                    className="flex items-center justify-between py-0.5 rounded transition-colors"
                  >
                    <Link
                      to={`/dictionary/${dict.currentHistoryId}`}
                      className="flex-1 text-gray-700 font-medium text-xs hover:underline"
                    >
                      {dict.title}
                    </Link>
                    <span
                      className={`text-[10px] px-1.5 py-0.5 rounded font-medium ${statusInfo.className}`}
                    >
                      {statusInfo.text}
                    </span>
                  </li>
                );
              })}
            </ul>
          </div>
        ))}

        {/* 구분선 */}
        <div className="col-span-full border-t border-gray-200 my-6"></div>

        {/* 영문 그룹 */}
        {dictionaryGroups
          .slice(
            koreanInitials.length,
            koreanInitials.length + englishInitials.length
          )
          .map((group) => (
            <div
              key={group.initial}
              className="border border-gray-200 rounded-lg p-4 bg-white"
            >
              <h3 className="text-sm font-bold mb-1 border-b pb-0.5 bg-gray-700 text-white -mx-4 -mt-4 px-4 pt-1.5 rounded-t-lg">
                {group.initial}
              </h3>
              <ul className="space-y-0.5">
                {group.dictionaries.map((dict) => {
                  const statusInfo = getStatusInfo(dict.status);
                  return (
                    <li
                      key={dict.currentHistoryId}
                      className="flex items-center justify-between py-0.5 rounded transition-colors"
                    >
                      <Link
                        to={`/dictionary/${dict.currentHistoryId}`}
                        className="flex-1 text-gray-700 font-medium text-xs hover:underline"
                      >
                        {dict.title}
                      </Link>
                      <span
                        className={`text-[10px] px-1.5 py-0.5 rounded font-medium ${statusInfo.className}`}
                      >
                        {statusInfo.text}
                      </span>
                    </li>
                  );
                })}
              </ul>
            </div>
          ))}

        {/* 구분선 */}
        <div className="col-span-full border-t border-gray-200 my-6"></div>

        {/* 숫자 그룹 */}
        {dictionaryGroups
          .slice(koreanInitials.length + englishInitials.length)
          .map((group) => (
            <div
              key={group.initial}
              className="border border-gray-200 rounded-lg p-4 bg-white"
            >
              <h3 className="text-sm font-bold mb-1 border-b pb-0.5 bg-gray-700 text-white -mx-4 -mt-4 px-4 pt-1.5 rounded-t-lg">
                {group.initial}
              </h3>
              <ul className="space-y-0.5">
                {group.dictionaries.map((dict) => {
                  const statusInfo = getStatusInfo(dict.status);
                  return (
                    <li
                      key={dict.currentHistoryId}
                      className="flex items-center justify-between py-0.5 rounded transition-colors"
                    >
                      <Link
                        to={`/dictionary/${dict.currentHistoryId}`}
                        className="flex-1 text-gray-700 font-medium text-xs hover:underline"
                      >
                        {dict.title}
                      </Link>
                      <span
                        className={`text-[10px] px-1.5 py-0.5 rounded font-medium ${statusInfo.className}`}
                      >
                        {statusInfo.text}
                      </span>
                    </li>
                  );
                })}
              </ul>
            </div>
          ))}
      </div>
    </>
  );
}

export default DictionaryCategoryPage;
