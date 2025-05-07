import { Link } from "react-router-dom";
import SEO from "../components/SEO";

const NotFoundPage = () => {
  return (
    <main className="flex flex-col items-center justify-center min-h-[60vh] text-center">
      <SEO
        title="404"
        description="요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다. 테일즈위키의 메인 페이지로 돌아가세요."
      />
      <h1 className="text-6xl font-bold text-gray-800 mb-4">404</h1>
      <h2 className="text-2xl font-semibold text-gray-600 mb-6">
        페이지를 찾을 수 없습니다.
      </h2>
      <p className="text-gray-500 mb-8">
        요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
      </p>
      <Link
        to="/"
        className="px-6 py-2 bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors"
        aria-label="메인 페이지로 돌아가기"
      >
        돌아가기
      </Link>
    </main>
  );
};

export default NotFoundPage;
