import React from "react";
import { FcGoogle } from "react-icons/fc";
import { RiKakaoTalkFill } from "react-icons/ri";

function LoginPage() {
  const handleKakaoLogin = () => {
    window.location.href = `https://kauth.kakao.com/oauth/authorize?client_id=${
      import.meta.env.VITE_KAKAO_CLIENT_ID
    }&redirect_uri=${
      import.meta.env.VITE_KAKAO_REDIRECT_URI
    }&response_type=code&scope=openid`;
  };

  const handleGoogleLogin = () => {
    window.location.href = `https://accounts.google.com/o/oauth2/auth?client_id=${
      import.meta.env.VITE_GOOGLE_CLIENT_ID
    }&redirect_uri=${
      import.meta.env.VITE_GOOGLE_REDIRECT_URI
    }&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email`;
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] text-center">
      <div className="w-full max-w-md p-8 bg-white rounded-lg">
        <h2 className="text-3xl font-bold text-gray-800 mb-8">로그인</h2>
        <button
          onClick={handleKakaoLogin}
          className="w-full flex items-center justify-center gap-2 px-6 py-3 mb-4 bg-yellow-300 text-gray-900 rounded-lg hover:bg-yellow-400 transition-colors text-lg font-bold"
        >
          <RiKakaoTalkFill size={28} className="mr-2" />
          카카오 로그인
        </button>
        <button
          onClick={handleGoogleLogin}
          className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-white text-gray-900 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors text-lg font-bold"
        >
          <FcGoogle size={28} className="mr-2" />
          구글 로그인
        </button>
      </div>
    </div>
  );
}

export default LoginPage;
