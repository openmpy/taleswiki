import React, { useState } from "react";
import { IoClose } from "react-icons/io5";
import { Link } from "react-router-dom";

function Header() {
  const [searchTerm, setSearchTerm] = useState("");

  const handleClearSearch = () => {
    setSearchTerm("");
  };

  return (
    <header className="bg-gray-800 text-white p-4 border-b border-gray-700">
      <div className="container mx-auto flex items-center justify-between">
        {/* 왼쪽 타이틀 */}
        <div className="flex-shrink-0">
          <Link to="/" className="hover:text-blue-400 transition-colors">
            <h1 className="text-2xl font-bold">테일즈위키</h1>
          </Link>
        </div>

        {/* 가운데 검색바 */}
        <div className="flex-1 max-w-2xl mx-8 relative">
          <input
            type="search"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="검색 하실 사전 제목을 입력해주세요"
            className="w-full px-4 py-2 rounded-lg bg-gray-700 text-white placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-blue-500 [&::-webkit-search-cancel-button]:hidden"
          />
          {searchTerm && (
            <button
              onClick={handleClearSearch}
              className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white transition-colors"
            >
              <IoClose size={20} />
            </button>
          )}
        </div>

        {/* 오른쪽 메뉴 */}
        <div className="flex-shrink-0 space-x-4">
          <Link
            to="/runner-dictionary"
            className="hover:text-blue-400 transition-colors"
          >
            런너사전
          </Link>
          <Link
            to="/guild-dictionary"
            className="hover:text-blue-400 transition-colors"
          >
            길드사전
          </Link>
        </div>
      </div>
    </header>
  );
}

export default Header;
