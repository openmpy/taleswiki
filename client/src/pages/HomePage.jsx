import React from "react";
import {
  FaExclamationTriangle,
  FaLightbulb,
  FaQuestionCircle,
  FaStar,
} from "react-icons/fa";
import SEO from "../components/SEO";

function HomePage() {
  return (
    <main>
      <SEO
        title="홈"
        description="테일즈위키의 모든 정보를 한 곳에서 확인하세요. 커뮤니티 규칙, 신고하기, 문의하기, 건의하기 등 다양한 기능을 제공합니다."
      />
      <header>
        <h1 className="text-xl font-semibold mb-4 flex items-center gap-2">
          <FaStar className="text-2xl text-yellow-400" aria-hidden="true" />
          테일즈위키
        </h1>
      </header>

      <section
        className="bg-white p-8 rounded-xl border border-gray-200 mb-6"
        aria-labelledby="community-rules"
      >
        <h2
          id="community-rules"
          className="text-xl font-semibold mb-6 text-gray-800"
        >
          커뮤니티 규칙
        </h2>
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
              <span
                className="w-2 h-2 bg-blue-400 rounded-full mr-3"
                aria-hidden="true"
              ></span>
              {rule}
            </li>
          ))}
        </ul>
        <p className="mt-6 text-sm text-gray-500 italic">
          위 규칙을 위반할 경우 관리자의 판단 하에 경고 또는 제재가 가해질 수
          있습니다.
        </p>
      </section>

      <section
        className="grid grid-cols-1 md:grid-cols-3 gap-6"
        aria-label="커뮤니티 메뉴"
      >
        <article className="bg-white p-6 rounded-xl border border-gray-200">
          <div className="flex items-center gap-3 mb-4">
            <FaExclamationTriangle
              className="text-2xl text-red-500"
              aria-hidden="true"
            />
            <h2 className="text-lg font-semibold text-gray-800">신고하기</h2>
          </div>
          <p className="text-gray-600 mb-4">
            규칙을 위반하는 게시물을 발견하셨나요?
          </p>
          <button
            className="w-full py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
            aria-label="규칙 위반 게시물 신고하기"
          >
            신고하기
          </button>
        </article>

        <article className="bg-white p-6 rounded-xl border border-gray-200">
          <div className="flex items-center gap-3 mb-4">
            <FaQuestionCircle
              className="text-2xl text-blue-500"
              aria-hidden="true"
            />
            <h2 className="text-lg font-semibold text-gray-800">문의하기</h2>
          </div>
          <p className="text-gray-600 mb-4">
            커뮤니티 이용 중 궁금한 점이 있으신가요?
          </p>
          <button
            className="w-full py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
            aria-label="커뮤니티 이용 문의하기"
          >
            문의하기
          </button>
        </article>

        <article className="bg-white p-6 rounded-xl border border-gray-200">
          <div className="flex items-center gap-3 mb-4">
            <FaLightbulb
              className="text-2xl text-yellow-500"
              aria-hidden="true"
            />
            <h2 className="text-lg font-semibold text-gray-800">건의하기</h2>
          </div>
          <p className="text-gray-600 mb-4">
            커뮤니티 개선을 위한 아이디어가 있으신가요?
          </p>
          <button
            className="w-full py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition-colors"
            aria-label="커뮤니티 개선 건의하기"
          >
            건의하기
          </button>
        </article>
      </section>
    </main>
  );
}

export default HomePage;
