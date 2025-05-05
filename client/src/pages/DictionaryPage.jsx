import React from "react";
import { BiBook } from "react-icons/bi";

function DictionaryPage({ type }) {
  const isRunnerDictionary = type === "runner";

  return (
    <>
      <div className="flex justify-between items-center mb-4">
        <div className="flex items-center gap-2">
          <BiBook className="text-xl" />
          <h2 className="text-xl font-semibold">
            {isRunnerDictionary ? "런너 사전" : "길드 사전"}
          </h2>
        </div>
        <button className="px-3 py-1.5 text-sm bg-gray-700 text-white rounded hover:bg-gray-800 transition-colors">
          작성하기
        </button>
      </div>
      <p>{isRunnerDictionary ? "런너 사전 내용" : "길드 사전 내용"}</p>
    </>
  );
}

export default DictionaryPage;
