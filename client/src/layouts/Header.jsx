import React, { useState } from "react";
import { FiMenu, FiSearch } from "react-icons/fi";
import { IoClose } from "react-icons/io5";
import { Link } from "react-router-dom";

function Header() {
  const [searchTerm, setSearchTerm] = useState("");
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isMobileSearchOpen, setIsMobileSearchOpen] = useState(false);

  const handleClearSearch = () => {
    setSearchTerm("");
  };

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
    setIsMobileSearchOpen(false);
  };

  const toggleMobileSearch = () => {
    setIsMobileSearchOpen(!isMobileSearchOpen);
    setIsMobileMenuOpen(false);
  };

  return (
    <header className="bg-gray-800 text-white p-4 border-b border-gray-700 relative">
      <div className="container mx-auto flex items-center justify-between">
        {/* 왼쪽 타이틀 */}
        <div className="flex-shrink-0">
          <Link to="/" className="hover:text-blue-400 transition-colors">
            <h1 className="text-2xl font-bold">테일즈위키</h1>
          </Link>
        </div>

        {/* 가운데 검색바 - 데스크톱 */}
        <div className="flex-1 max-w-2xl mx-8 relative hidden md:block">
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

        {/* 오른쪽 메뉴 - 데스크톱 */}
        <div className="hidden md:flex flex-shrink-0 space-x-4 justify-center">
          <Link
            to="/dictionary/runner"
            className="hover:text-blue-400 transition-colors"
          >
            런너사전
          </Link>
          <Link
            to="/dictionary/guild"
            className="hover:text-blue-400 transition-colors"
          >
            길드사전
          </Link>
        </div>

        {/* 모바일 메뉴 버튼들 */}
        <div className="md:hidden flex items-center space-x-4">
          <button
            className="text-white hover:text-blue-400 transition-colors"
            onClick={toggleMobileSearch}
          >
            <FiSearch size={24} />
          </button>
          <button
            className="text-white hover:text-blue-400 transition-colors"
            onClick={toggleMobileMenu}
          >
            <FiMenu size={24} />
          </button>
        </div>
      </div>

      {/* 모바일 검색창 */}
      {isMobileSearchOpen && (
        <div className="md:hidden absolute top-full left-0 right-0 bg-gray-800 border-b border-gray-700 px-4 pt-2 pb-4 z-50">
          <div className="relative">
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
        </div>
      )}

      {/* 모바일 메뉴 */}
      {isMobileMenuOpen && (
        <div className="md:hidden absolute top-full left-0 right-0 bg-gray-800 border-b border-gray-700 px-4 pb-4 z-50">
          <div className="flex flex-col items-center">
            <Link
              to="/runner-dictionary"
              className="w-full py-2 hover:text-blue-400 transition-colors text-center border-b border-gray-700"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              런너사전
            </Link>
            <Link
              to="/guild-dictionary"
              className="w-full py-2 hover:text-blue-400 transition-colors text-center"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              길드사전
            </Link>
          </div>
        </div>
      )}
    </header>
  );
}

export default Header;
