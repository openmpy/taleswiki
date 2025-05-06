import axios from "axios";
import { useState } from "react";

function AdminLogin({ onLoginSuccess }) {
  const [nickname, setNickname] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/admin/signin",
        { nickname, password },
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      if (response.status === 200) {
        onLoginSuccess();
      }
    } catch (error) {
      alert(error.response.data.message);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] text-center">
      <div className="w-full max-w-md p-8 bg-white rounded-lg">
        <h2 className="text-3xl font-bold text-gray-800 mb-6">관리자 로그인</h2>
        <form onSubmit={handleSubmit} className="space-y-6 text-left">
          <div>
            <label
              className="block text-gray-700 text-sm font-semibold mb-2"
              htmlFor="nickname"
            >
              아이디
            </label>
            <input
              type="text"
              id="nickname"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-gray-700"
              required
            />
          </div>
          <div>
            <label
              className="block text-gray-700 text-sm font-semibold mb-2"
              htmlFor="password"
            >
              비밀번호
            </label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-gray-700"
              required
            />
          </div>
          <button
            type="submit"
            className="w-full px-6 py-3 bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors"
          >
            로그인
          </button>
        </form>
      </div>
    </div>
  );
}

export default AdminLogin;
