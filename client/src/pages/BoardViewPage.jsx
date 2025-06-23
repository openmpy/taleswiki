import "@toast-ui/editor/dist/toastui-editor-viewer.css";
import { Viewer } from "@toast-ui/react-editor";
import React, { useEffect, useRef, useState } from "react";
import { BiMessageSquareDetail } from "react-icons/bi";
import { FaRegThumbsDown, FaRegThumbsUp } from "react-icons/fa";
import { useNavigate, useParams } from "react-router-dom";
import LoadingSpinner from "../components/LoadingSpinner";
import axiosInstance from "../utils/axiosConfig";
import { formatKoreanDateTime } from "../utils/dateUtils";

const BoardViewPage = () => {
  const { boardId } = useParams();
  const navigate = useNavigate();
  const [board, setBoard] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const viewerRef = useRef(null);
  const menuRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  useEffect(() => {
    const fetchBoard = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const response = await axiosInstance.get(`/api/v1/boards/${boardId}`);
        setBoard(response.data);
      } catch (err) {
        if (err.response?.status === 401) {
          alert("로그인이 필요합니다.");
          navigate("/login");
          return;
        }
        if (err.response?.status === 404) {
          setError("게시글을 찾을 수 없습니다.");
        } else {
          console.error("게시글을 불러오는데 실패했습니다:", err);
          setError("게시글을 불러오는데 실패했습니다.");
        }
      } finally {
        setIsLoading(false);
      }
    };

    const fetchCurrentUser = async () => {
      const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";

      if (!isLoggedIn) {
        return;
      }

      try {
        const response = await axiosInstance.get("/api/v1/members/me");
        setCurrentUser(response.data);
      } catch (err) {
        console.error("현재 사용자 정보를 가져오는데 실패했습니다:", err);
      }
    };

    fetchBoard();
    fetchCurrentUser();
  }, [boardId, navigate]);

  const handleVoteAction = async (action) => {
    const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";
    if (!isLoggedIn) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }

    const actionKorean = action === "like" ? "추천" : "비추천";

    try {
      await axiosInstance.post(`/api/v1/boards/${action}/${boardId}`);
      const response = await axiosInstance.get(`/api/v1/boards/${boardId}`);
      setBoard(response.data);
    } catch (err) {
      if (err.response?.status === 401) {
        alert("로그인이 필요합니다.");
        navigate("/login");
      } else if (err.response?.status === 400) {
        alert(err.response.data.message || "요청 처리에 실패했습니다.");
      } else {
        console.error(`${actionKorean} 처리에 실패했습니다:`, err);
        alert(`${actionKorean} 처리에 실패했습니다.`);
      }
    }
  };

  const handleEdit = () => {
    navigate(`/board/${boardId}/edit`);
  };

  const handleDelete = async () => {
    if (!confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
      return;
    }

    try {
      await axiosInstance.delete(`/api/v1/boards/${boardId}`);
      alert("게시글이 삭제되었습니다.");
      navigate("/community");
    } catch (err) {
      console.error("게시글 삭제에 실패했습니다:", err);
      alert("게시글 삭제에 실패했습니다.");
    }
  };

  const isAuthor =
    currentUser && board && currentUser.memberId === board.memberId;

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="text-red-500 font-medium">{error}</div>
      </div>
    );
  }

  if (!board) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="text-gray-500 font-medium">
          게시글을 찾을 수 없습니다.
        </div>
      </div>
    );
  }

  return (
    <main>
      <header className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4">
        <div className="flex items-center gap-2">
          <h1 className="text-xl font-semibold flex items-center gap-2">
            <BiMessageSquareDetail
              className="text-2xl text-gray-700"
              aria-hidden="true"
            />
            커뮤니티
          </h1>
        </div>
        <nav className="flex flex-col sm:flex-row gap-2 mt-2 sm:mt-0">
          <button
            className="w-full sm:w-auto px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-gray-100 text-gray-700 hover:bg-gray-200"
            onClick={() => navigate("/community")}
            aria-label="이전 페이지로 돌아가기"
          >
            뒤로가기
          </button>
          <div className="relative" ref={menuRef}>
            <button
              className="w-full sm:hidden px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-blue-100 text-blue-700 hover:bg-blue-200"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              aria-label="메뉴 열기"
            >
              메뉴
            </button>
            {isMenuOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 z-10">
                <div className="py-1">
                  {isAuthor && (
                    <>
                      <button
                        className="w-full px-4 py-2 text-sm text-left hover:bg-red-50"
                        onClick={() => {
                          handleDelete();
                          setIsMenuOpen(false);
                        }}
                      >
                        삭제하기
                      </button>
                      <button
                        className="w-full px-4 py-2 text-sm text-left hover:bg-blue-50"
                        onClick={() => {
                          handleEdit();
                          setIsMenuOpen(false);
                        }}
                      >
                        수정하기
                      </button>
                    </>
                  )}
                  <button
                    className="w-full px-4 py-2 text-sm text-left hover:bg-gray-100"
                    onClick={() => {
                      navigate("/board/write");
                      setIsMenuOpen(false);
                    }}
                  >
                    작성하기
                  </button>
                </div>
              </div>
            )}
            <div className="hidden sm:flex gap-2">
              {isAuthor && (
                <>
                  <button
                    onClick={handleDelete}
                    className="px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-red-100 text-red-700 hover:bg-red-200"
                  >
                    삭제하기
                  </button>
                  <button
                    onClick={handleEdit}
                    className="px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-blue-100 text-blue-700 hover:bg-blue-200"
                  >
                    수정하기
                  </button>
                </>
              )}
              <button
                className="px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-gray-700 text-white hover:bg-gray-800"
                onClick={() => navigate("/board/write")}
                aria-label="게시글 작성하기"
              >
                작성하기
              </button>
            </div>
          </div>
        </nav>
      </header>

      {/* 게시글 헤더 */}
      <header className="border-b border-gray-200 pb-4 mb-6">
        <div className="border-t border-gray-400 pt-4 mb-2">
          <h2 className="text-xl font-semibold text-gray-900">{board.title}</h2>
        </div>
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 text-sm text-gray-500">
          <div className="flex items-center gap-2">
            <span>{board.author}</span>
            <span className="text-gray-300">|</span>
            <span>{formatKoreanDateTime(board.createdAt)}</span>
          </div>
          <div className="flex items-center gap-2">
            <span>조회수 {board.view.toLocaleString()}</span>
            <span className="text-gray-300">|</span>
            <span>추천수 {board.likes.toLocaleString()}</span>
          </div>
        </div>
      </header>

      {/* 게시글 내용 */}
      <article>
        <div className="toastui-editor-viewer" ref={viewerRef}>
          <Viewer initialValue={board.content || "게시글 내용이 없습니다."} />
        </div>
      </article>

      {/* 추천/비추천 버튼 */}
      <div className="flex justify-center items-center gap-2 my-6">
        <button
          onClick={() => handleVoteAction("like")}
          className="flex items-center gap-1 px-3 py-1.5 rounded-full border border-gray-300 hover:bg-gray-100 transition-colors"
          aria-label={`추천 ${board.likes.toLocaleString()}개`}
        >
          <FaRegThumbsUp className="text-blue-500 text-lg" />
          <span className="font-medium text-gray-700 text-sm">
            추천 {board.like.toLocaleString()}
          </span>
        </button>
        <button
          onClick={() => handleVoteAction("unlike")}
          className="flex items-center gap-1 px-3 py-1.5 rounded-full border border-gray-300 hover:bg-gray-100 transition-colors"
          aria-label={`비추천 ${board.unlike?.toLocaleString() || 0}개`}
        >
          <FaRegThumbsDown className="text-red-500 text-lg" />
          <span className="font-medium text-gray-700 text-sm">
            비추천 {board.unlike?.toLocaleString() || 0}
          </span>
        </button>
      </div>
    </main>
  );
};

export default BoardViewPage;
