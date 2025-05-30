import React, { useEffect, useState } from "react";
import { BiBook } from "react-icons/bi";
import { Link, useNavigate } from "react-router-dom";
import LoadingSpinner from "../components/LoadingSpinner";
import SEO from "../components/SEO";
import axiosInstance from "../utils/axiosConfig";
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
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDictionaries = async () => {
      try {
        setIsLoading(true);
        const category = isRunnerDictionary ? "person" : "guild";
        const response = await axiosInstance.get(
          `/api/v1/dictionaries/categories/${category}`
        );
        const data = response.data;

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
      } finally {
        setIsLoading(false);
      }
    };

    fetchDictionaries();
  }, [isRunnerDictionary]);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  const pageTitle = isRunnerDictionary ? "런너 사전" : "길드 사전";

  return (
    <main>
      <SEO
        title={pageTitle}
        description={"누구나 문서를 조회, 작성, 편집할 수 있습니다."}
      />
      <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-4">
        <div className="flex items-center gap-2">
          <h1 className="text-xl font-semibold flex items-center gap-2">
            <BiBook className="text-2xl text-gray-700" aria-hidden="true" />
            {pageTitle}
          </h1>
        </div>
        <button
          onClick={() =>
            navigate(
              `/dictionary/write?type=${
                isRunnerDictionary ? "runner" : "guild"
              }`
            )
          }
          className="w-full sm:w-auto px-4 py-2 text-sm font-medium bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors"
          aria-label={`${pageTitle} 작성하기`}
        >
          작성하기
        </button>
      </header>

      <section
        className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4"
        aria-label="문서 목록"
      >
        {/* 한글 그룹 */}
        {dictionaryGroups.slice(0, koreanInitials.length).map((group) => (
          <article
            key={group.initial}
            className="border border-gray-200 rounded-lg p-4 bg-white"
          >
            <h2 className="text-sm font-bold mb-1 border-b pb-0.5 bg-gray-700 text-white -mx-4 -mt-4 px-4 pt-1.5 rounded-t-lg">
              {group.initial}
            </h2>
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
                      className="flex-1 text-gray-700 font-medium text-xs hover:underline truncate"
                      aria-label={`${dict.title} ${statusInfo.text}`}
                    >
                      {dict.title}
                    </Link>
                    <span
                      className={`text-[10px] px-1.5 py-0.5 rounded font-medium ${statusInfo.className}`}
                      aria-hidden="true"
                    >
                      {statusInfo.text}
                    </span>
                  </li>
                );
              })}
            </ul>
          </article>
        ))}

        {/* 구분선 */}
        <div
          className="col-span-full border-t border-gray-200 my-6"
          aria-hidden="true"
        ></div>

        {/* 영문 그룹 */}
        {dictionaryGroups
          .slice(
            koreanInitials.length,
            koreanInitials.length + englishInitials.length
          )
          .map((group) => (
            <article
              key={group.initial}
              className="border border-gray-200 rounded-lg p-4 bg-white"
            >
              <h2 className="text-sm font-bold mb-1 border-b pb-0.5 bg-gray-700 text-white -mx-4 -mt-4 px-4 pt-1.5 rounded-t-lg">
                {group.initial}
              </h2>
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
                        className="flex-1 text-gray-700 font-medium text-xs hover:underline truncate"
                        aria-label={`${dict.title} ${statusInfo.text}`}
                      >
                        {dict.title}
                      </Link>
                      <span
                        className={`text-[10px] px-1.5 py-0.5 rounded font-medium ${statusInfo.className}`}
                        aria-hidden="true"
                      >
                        {statusInfo.text}
                      </span>
                    </li>
                  );
                })}
              </ul>
            </article>
          ))}

        {/* 구분선 */}
        <div
          className="col-span-full border-t border-gray-200 my-6"
          aria-hidden="true"
        ></div>

        {/* 숫자 그룹 */}
        {dictionaryGroups
          .slice(koreanInitials.length + englishInitials.length)
          .map((group) => (
            <article
              key={group.initial}
              className="border border-gray-200 rounded-lg p-4 bg-white"
            >
              <h2 className="text-sm font-bold mb-1 border-b pb-0.5 bg-gray-700 text-white -mx-4 -mt-4 px-4 pt-1.5 rounded-t-lg">
                {group.initial}
              </h2>
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
                        className="flex-1 text-gray-700 font-medium text-xs hover:underline truncate"
                        aria-label={`${dict.title} ${statusInfo.text}`}
                      >
                        {dict.title}
                      </Link>
                      <span
                        className={`text-[10px] px-1.5 py-0.5 rounded font-medium ${statusInfo.className}`}
                        aria-hidden="true"
                      >
                        {statusInfo.text}
                      </span>
                    </li>
                  );
                })}
              </ul>
            </article>
          ))}
      </section>
    </main>
  );
}

export default DictionaryCategoryPage;
