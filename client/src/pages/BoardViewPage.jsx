import "@toast-ui/editor/dist/toastui-editor-viewer.css";
import { Viewer } from "@toast-ui/react-editor";
import React, { useEffect, useRef, useState } from "react";
import { BiMessageSquareDetail } from "react-icons/bi";
import { useNavigate, useParams } from "react-router-dom";
import LoadingSpinner from "../components/LoadingSpinner";
import axiosInstance from "../utils/axiosConfig";
import { formatKoreanDateTime } from "../utils/dateUtils";

const BoardViewPage = () => {
  const { boardId } = useParams();
  const navigate = useNavigate();
  const [board, setBoard] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const viewerRef = useRef(null);

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

    fetchBoard();
  }, [boardId, navigate]);

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
            onClick={() => navigate(-1)}
            aria-label="이전 페이지로 돌아가기"
          >
            뒤로가기
          </button>
          <button
            className="w-full sm:w-auto px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-gray-700 text-white hover:bg-gray-800"
            onClick={() => navigate("/board/write")}
            aria-label="게시글 작성하기"
          >
            작성하기
          </button>
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
          <div className="flex items-center gap-4">
            <span>조회수 {board.view.toLocaleString()}</span>
          </div>
        </div>
      </header>

      {/* 게시글 내용 */}
      <article>
        <div className="toastui-editor-viewer" ref={viewerRef}>
          <Viewer initialValue={board.content || "게시글 내용이 없습니다."} />
        </div>
      </article>
    </main>
  );
};

export default BoardViewPage;
