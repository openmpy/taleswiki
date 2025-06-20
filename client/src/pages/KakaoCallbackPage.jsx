import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import LoadingSpinner from "../components/LoadingSpinner";
import axiosInstance from "../utils/axiosConfig";

function KakaoCallbackPage() {
  const [searchParams] = useSearchParams();
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const code = searchParams.get("code");
    if (!code) {
      setError("인가 코드가 없습니다.");
      return;
    }

    axiosInstance
      .get("/api/v1/members/login/kakao", {
        params: { code },
      })
      .then(() => {
        localStorage.setItem("isLoggedIn", "true");
        window.dispatchEvent(new Event("storage"));
        navigate("/");
      })
      .catch((e) => {
        setError(
          e.response?.data?.message || "로그인 처리 중 오류가 발생했습니다."
        );
      });
  }, [searchParams, navigate]);

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] text-center">
        <div className="w-full max-w-md p-8 bg-white rounded-lg">
          <h2 className="text-2xl font-bold text-red-600 mb-4">로그인 실패</h2>
          <p className="text-gray-700">{error}</p>
        </div>
      </div>
    );
  }

  return <LoadingSpinner />;
}

export default KakaoCallbackPage;
