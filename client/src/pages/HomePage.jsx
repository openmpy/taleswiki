import React from "react";
import { FaStar } from "react-icons/fa";

function HomePage() {
  return (
    <>
      <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
        <FaStar className="text-2xl text-yellow-400" />
        테일즈위키
      </h2>

      <div className="bg-white p-8 rounded-xl border border-blue-100">
        <h3 className="text-xl font-semibold mb-6 text-gray-800">
          커뮤니티 규칙
        </h3>
        <ul className="space-y-3">
          {[
            "개인정보(신상정보) 공개 금지",
            "욕설, 비방, 혐오 발언 금지",
            "타인을 비하하거나 모욕하는 발언 금지",
            "광고성 게시물 및 스팸 금지",
            "저작권 침해 게시물 금지",
            "목적에 맞지 않는 게시물 금지",
          ].map((rule, index) => (
            <li key={index} className="flex items-center text-gray-700">
              <span className="w-2 h-2 bg-blue-400 rounded-full mr-3"></span>
              {rule}
            </li>
          ))}
        </ul>
        <p className="mt-6 text-sm text-gray-500 italic">
          위 규칙을 위반할 경우 관리자의 판단 하에 경고 또는 제재가 가해질 수
          있습니다.
        </p>
      </div>
    </>
  );
}

export default HomePage;
