import React, { useEffect, useRef, useState } from "react";
import { BsShuffle } from "react-icons/bs";
import { FiMenu, FiSearch } from "react-icons/fi";
import { IoClose } from "react-icons/io5";
import { Link, useNavigate } from "react-router-dom";
import axiosInstance from "../utils/axiosConfig";

function Header() {
  const [searchTerm, setSearchTerm] = useState("");
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isMobileSearchOpen, setIsMobileSearchOpen] = useState(false);
  const [searchResults, setSearchResults] = useState([]);
  const [isLoggedIn, setIsLoggedIn] = useState(
    localStorage.getItem("isLoggedIn") === "true"
  );
  const navigate = useNavigate();
  const searchRef = useRef(null);

  useEffect(() => {
    const handleStorage = () => {
      setIsLoggedIn(localStorage.getItem("isLoggedIn") === "true");
    };
    window.addEventListener("storage", handleStorage);
    return () => window.removeEventListener("storage", handleStorage);
  }, []);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setSearchResults([]);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  useEffect(() => {
    const delayDebounceFn = setTimeout(async () => {
      if (searchTerm.trim()) {
        try {
          const response = await axiosInstance.get(
            "/api/v1/dictionaries/search",
            {
              params: { title: searchTerm },
            }
          );
          setSearchResults(response.data.hits);
        } catch (error) {
          console.error("검색 중 오류 발생:", error);
          setSearchResults([]);
        }
      } else {
        setSearchResults([]);
      }
    }, 300);

    return () => clearTimeout(delayDebounceFn);
  }, [searchTerm]);

  const handleClearSearch = () => {
    setSearchTerm("");
    setSearchResults([]);
  };

  const handleResultClick = (dictionary) => {
    setSearchTerm("");
    setSearchResults([]);
    setIsMobileSearchOpen(false);
    navigate(`/dictionary/${dictionary.currentHistoryId}`);
  };

  const handleShuffle = async () => {
    try {
      const response = await axiosInstance.get("/api/v1/dictionaries/random");
      if (response.data && response.data.currentHistoryId) {
        navigate(`/dictionary/${response.data.currentHistoryId}`);
      }
    } catch (error) {
      console.error("랜덤 사전 조회 중 오류 발생:", error);
    }
  };

  const handleLogout = async () => {
    try {
      await axiosInstance.post("/api/v1/members/logout");
      localStorage.removeItem("isLoggedIn");
      setIsLoggedIn(false);
      navigate("/");
    } catch {
      alert("로그아웃 중 오류가 발생했습니다.");
    }
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
        <div
          className="flex-1 max-w-2xl mx-8 relative hidden md:block"
          ref={searchRef}
        >
          <div className="relative">
            <div className="flex items-center gap-2">
              <div className="relative flex-1">
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
                {/* 검색 결과 드롭다운 */}
                {searchResults.length > 0 && (
                  <div className="absolute w-full mt-1 bg-gray-700 rounded-lg shadow-lg z-50 max-h-[300px] overflow-y-auto">
                    {searchResults.map((result) => (
                      <button
                        key={result.currentHistoryId}
                        onClick={() => handleResultClick(result)}
                        className="w-full px-4 py-2 text-left hover:bg-gray-600 transition-colors border-b border-gray-600 last:border-b-0"
                      >
                        <div className="flex items-center gap-2">
                          <span
                            className={`text-xs px-2 py-0.5 rounded whitespace-nowrap ${
                              result.category === "런너"
                                ? "bg-blue-100 text-blue-800"
                                : "bg-purple-100 text-purple-800"
                            }`}
                          >
                            {result.category}
                          </span>
                          <span className="text-gray-200">{result.title}</span>
                        </div>
                      </button>
                    ))}
                  </div>
                )}
              </div>
              <button
                className="p-2 rounded-lg bg-gray-700 text-gray-400 hover:text-white hover:bg-gray-600 transition-colors"
                onClick={handleShuffle}
              >
                <BsShuffle size={20} />
              </button>
            </div>
          </div>
        </div>

        {/* 오른쪽 메뉴 - 데스크톱 */}
        <div className="hidden md:flex flex-shrink-0 space-x-4 justify-center">
          <Link
            to="/community"
            className="hover:text-blue-400 transition-colors"
          >
            커뮤니티
          </Link>
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
          {isLoggedIn ? (
            <>
              <Link
                to="/mypage"
                className="hover:text-blue-400 transition-colors"
              >
                마이페이지
              </Link>
              <button
                onClick={handleLogout}
                className="hover:text-blue-400 transition-colors"
              >
                로그아웃
              </button>
            </>
          ) : (
            <Link to="/login" className="hover:text-blue-400 transition-colors">
              로그인
            </Link>
          )}
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
            onClick={handleShuffle}
          >
            <BsShuffle size={24} />
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
          <div className="relative" ref={searchRef}>
            <div className="flex items-center gap-2">
              <div className="relative flex-1">
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
                {/* 모바일 검색 결과 */}
                {searchResults.length > 0 && (
                  <div className="absolute w-full mt-1 bg-gray-700 rounded-lg shadow-lg z-50 max-h-[300px] overflow-y-auto">
                    {searchResults.map((result) => (
                      <button
                        key={result.currentHistoryId}
                        onClick={() => handleResultClick(result)}
                        className="w-full px-4 py-2 text-left hover:bg-gray-600 transition-colors border-b border-gray-600 last:border-b-0"
                      >
                        <div className="flex items-center gap-2">
                          <span
                            className={`text-xs px-2 py-0.5 rounded whitespace-nowrap ${
                              result.category === "런너"
                                ? "bg-blue-100 text-blue-800"
                                : "bg-purple-100 text-purple-800"
                            }`}
                          >
                            {result.category}
                          </span>
                          <span className="text-gray-200">{result.title}</span>
                        </div>
                      </button>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 모바일 메뉴 */}
      {isMobileMenuOpen && (
        <div className="md:hidden absolute top-full left-0 right-0 bg-gray-800 border-b border-gray-700 px-4 pb-4 z-50">
          <div className="flex flex-col items-center">
            <Link
              to="/community"
              className="w-full py-2 hover:text-blue-400 transition-colors text-center border-b border-gray-700"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              커뮤니티
            </Link>
            <Link
              to="/dictionary/runner"
              className="w-full py-2 hover:text-blue-400 transition-colors text-center border-b border-gray-700"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              런너사전
            </Link>
            <Link
              to="/dictionary/guild"
              className="w-full py-2 hover:text-blue-400 transition-colors text-center border-b border-gray-700"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              길드사전
            </Link>
            {isLoggedIn ? (
              <>
                <Link
                  to="/mypage"
                  className="w-full py-2 hover:text-blue-400 transition-colors text-center border-b border-gray-700"
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  마이페이지
                </Link>
                <button
                  onClick={() => {
                    handleLogout();
                    setIsMobileMenuOpen(false);
                  }}
                  className="w-full py-2 hover:text-blue-400 transition-colors text-center"
                >
                  로그아웃
                </button>
              </>
            ) : (
              <Link
                to="/login"
                className="w-full py-2 hover:text-blue-400 transition-colors text-center"
                onClick={() => setIsMobileMenuOpen(false)}
              >
                로그인
              </Link>
            )}
          </div>
        </div>
      )}
    </header>
  );
}

export default Header;
