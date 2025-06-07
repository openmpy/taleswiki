import React from "react";
import {
  FaExclamationTriangle,
  FaLightbulb,
  FaQuestionCircle,
  FaStar,
} from "react-icons/fa";

function HomePage() {
  return (
    <main>
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
            "광고성 게시물 및 스팸 금지",
            "목적에 맞지 않는 게시물 금지",
            "저작권 침해 게시물 금지",
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
        <button
          className="w-full py-4 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors flex items-center justify-center gap-2"
          aria-label="규칙 위반 게시물 신고하기"
          onClick={() =>
            window.open("https://forms.gle/a17rFUFizVgpEN6m8", "_blank")
          }
        >
          <FaExclamationTriangle className="text-xl" aria-hidden="true" />
          신고하기
        </button>

        <button
          className="w-full py-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center justify-center gap-2"
          aria-label="커뮤니티 이용 문의하기"
          onClick={() =>
            window.open("https://forms.gle/qae3XzuUprUVThwt5", "_blank")
          }
        >
          <FaQuestionCircle className="text-xl" aria-hidden="true" />
          문의하기
        </button>

        <button
          className="w-full py-4 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors flex items-center justify-center gap-2"
          aria-label="커뮤니티 개선 건의하기"
          onClick={() =>
            window.open("https://forms.gle/58VQgZJEGbBwDz9L6", "_blank")
          }
        >
          <FaLightbulb className="text-xl" aria-hidden="true" />
          건의하기
        </button>
      </section>
    </main>
  );
}

export default HomePage;
