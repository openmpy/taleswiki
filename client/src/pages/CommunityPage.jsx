import React, { useCallback, useEffect, useState } from "react";
import { BiFile, BiImage, BiMessageSquareDetail } from "react-icons/bi";
import { useNavigate } from "react-router-dom";
import Pagination from "../components/admin/Pagination";
import LoadingSpinner from "../components/LoadingSpinner";
import axiosInstance from "../utils/axiosConfig";
import { formatRelativeTime } from "../utils/dateUtils";

function CommunityPage() {
  const navigate = useNavigate();
  const [boards, setBoards] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchBoards = useCallback(
    async (page = 0) => {
      try {
        setLoading(true);
        setError(null);

        const response = await axiosInstance.get(
          `/api/v1/boards?page=${page}&size=30`
        );
        const { content, totalPages: pages } = response.data;

        setBoards(content);
        setTotalPages(pages);
      } catch (err) {
        if (err.response?.status === 401) {
          alert("로그인이 필요합니다.");
          navigate("/login");
          return;
        }
        console.error("게시글 목록을 불러오는데 실패했습니다:", err);
        setError("게시글 목록을 불러오는데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    },
    [navigate]
  );

  useEffect(() => {
    fetchBoards(currentPage);
  }, [fetchBoards, currentPage]);

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleBoardClick = (boardId) => {
    navigate(`/board/${boardId}`);
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="text-red-500 font-medium">{error}</div>
      </div>
    );
  }

  return (
    <main>
      {/* 헤더 */}
      <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-4">
        <div className="flex items-center gap-2">
          <h1 className="text-xl font-semibold flex items-center gap-2">
            <BiMessageSquareDetail
              className="text-2xl text-gray-700"
              aria-hidden="true"
            />
            커뮤니티
          </h1>
        </div>
        <button
          onClick={() => navigate("/board/write")}
          className="w-full sm:w-auto px-4 py-2 text-sm font-medium bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors"
          aria-label="게시글 작성하기"
        >
          작성하기
        </button>
      </header>

      {/* 게시글 목록 */}
      <section aria-label="게시글 목록">
        {boards.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-8 px-4 bg-gray-50 rounded-lg border border-gray-200">
            <div className="text-gray-500 font-medium">게시글이 없습니다.</div>
          </div>
        ) : (
          <>
            {/* 데스크톱 테이블 */}
            <div className="hidden md:block bg-white rounded-lg border border-gray-200 overflow-hidden">
              <table className="w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-20">
                      번호
                    </th>
                    <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                      제목
                    </th>
                    <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-40">
                      글쓴이
                    </th>
                    <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-30">
                      작성일
                    </th>
                    <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-20">
                      조회
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {boards.map((board) => (
                    <tr
                      key={board.boardId}
                      onClick={() => handleBoardClick(board.boardId)}
                      className="hover:bg-gray-50 cursor-pointer transition-colors"
                    >
                      <td className="px-4 py-3 text-sm text-gray-500 font-mono w-20 text-center">
                        {board.boardId}
                      </td>
                      <td className="px-4 py-3 text-sm font-medium text-gray-900 max-w-0">
                        <div className="flex items-center gap-2 min-w-0">
                          {board.hasImage ? (
                            <BiImage className="text-green-600 flex-shrink-0 text-lg" />
                          ) : (
                            <BiFile className="text-gray-400 flex-shrink-0 text-lg" />
                          )}
                          <span className="truncate">{board.title}</span>
                        </div>
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-500 w-40 text-center">
                        {board.nickname}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-500 w-24 text-center">
                        {formatRelativeTime(board.createdAt)}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-500 w-20 text-center">
                        {board.view.toLocaleString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* 모바일 카드 목록 */}
            <div className="md:hidden">
              {boards.map((board) => (
                <article
                  key={board.boardId}
                  onClick={() => handleBoardClick(board.boardId)}
                  className="bg-white border-b border-gray-200 p-3 hover:bg-gray-50 cursor-pointer transition-colors last:border-b-0"
                >
                  <h3 className="text-sm font-medium text-gray-900 mb-1">
                    <div className="flex items-center gap-1 min-w-0">
                      {board.hasImage ? (
                        <BiImage className="text-green-600 flex-shrink-0 text-sm" />
                      ) : (
                        <BiFile className="text-gray-400 flex-shrink-0 text-sm" />
                      )}
                      <span className="truncate">{board.title}</span>
                    </div>
                  </h3>
                  <div className="flex items-center text-xs text-gray-500 space-x-2">
                    <span>{board.nickname}</span>
                    <span className="text-gray-300">|</span>
                    <span>{formatRelativeTime(board.createdAt)}</span>
                    <span className="text-gray-300">|</span>
                    <span>조회 {board.view.toLocaleString()}</span>
                  </div>
                </article>
              ))}
            </div>
          </>
        )}
      </section>

      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <footer className="mt-6">
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </footer>
      )}
    </main>
  );
}

export default CommunityPage;
