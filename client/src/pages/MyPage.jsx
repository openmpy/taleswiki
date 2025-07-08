import React, { useEffect, useState } from "react";
import { BiFile, BiImage, BiMessageSquareDetail } from "react-icons/bi";
import { useNavigate } from "react-router-dom";
import Pagination from "../components/admin/Pagination";
import LoadingSpinner from "../components/LoadingSpinner";
import axiosInstance from "../utils/axiosConfig";
import { formatKoreanDateTime, formatRelativeTime } from "../utils/dateUtils";

function MyPage() {
  const [userInfo, setUserInfo] = useState({
    memberId: "",
    email: "",
    nickname: "",
    social: "",
    authority: "",
    createdAt: "",
  });
  const [newNickname, setNewNickname] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // 내가 작성한 게시글 관련 state
  const [myBoards, setMyBoards] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoadingBoards, setIsLoadingBoards] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";
    if (!isLoggedIn) {
      navigate("/login");
      return;
    }

    fetchUserInfo();
    fetchMyBoards();
  }, [navigate]);

  const fetchUserInfo = async () => {
    try {
      const response = await axiosInstance.get("/api/v1/members/me");
      setUserInfo(response.data);
      setNewNickname(response.data.nickname);
      setIsLoading(false);
    } catch (error) {
      console.error("사용자 정보 조회 중 오류 발생:", error);
      setError("사용자 정보를 불러오는데 실패했습니다.");
      setIsLoading(false);
    }
  };

  // 내가 작성한 게시글 가져오기
  const fetchMyBoards = async (page = 0) => {
    try {
      setIsLoadingBoards(true);
      const response = await axiosInstance.get(
        `/api/v1/boards/member?page=${page}&size=10`
      );
      setMyBoards(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error("내가 작성한 게시글 조회 중 오류 발생:", error);
      setError("내가 작성한 게시글을 불러오는데 실패했습니다.");
    } finally {
      setIsLoadingBoards(false);
    }
  };

  // 페이지 변경 핸들러
  const handlePageChange = (page) => {
    setCurrentPage(page);
    fetchMyBoards(page);
  };

  // 게시글 클릭 핸들러
  const handleBoardClick = (boardId) => {
    navigate(`/board/${boardId}`);
  };

  const handleNicknameChange = async () => {
    if (!newNickname.trim()) {
      setError("닉네임을 입력해주세요.");
      return;
    }

    if (newNickname === userInfo.nickname) {
      setError("현재 닉네임과 동일합니다.");
      return;
    }

    try {
      await axiosInstance.patch("/api/v1/members/nickname", {
        nickname: newNickname,
      });

      setUserInfo((prev) => ({ ...prev, nickname: newNickname }));
      setSuccess("닉네임이 성공적으로 변경되었습니다.");
      setIsEditing(false);
      setError("");

      // 3초 후 성공 메시지 제거
      setTimeout(() => setSuccess(""), 3000);
    } catch (error) {
      console.error("닉네임 변경 중 오류 발생:", error);
      let errorMessage = "닉네임 변경에 실패했습니다.";

      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }

      alert(errorMessage);
    }
  };

  const handleCancel = () => {
    setNewNickname(userInfo.nickname);
    setIsEditing(false);
    setError("");
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <main>
      {/* 헤더 */}
      <header className="mb-6">
        <h1 className="text-xl font-semibold flex items-center gap-2">
          <svg
            className="w-6 h-6 text-gray-700"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
            />
          </svg>
          마이페이지
        </h1>
      </header>

      {/* 성공 메시지 */}
      {success && (
        <div className="mb-6 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
          {success}
        </div>
      )}

      {/* 오류 메시지 */}
      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {/* 사용자 정보 섹션 */}
      <section className="bg-white p-8 rounded-xl border border-gray-200 mb-6">
        <h2 className="text-xl font-semibold mb-6 text-gray-800">기본 정보</h2>

        <div className="space-y-6">
          {/* 이메일 정보 */}
          <div className="border-b border-gray-200 pb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              이메일
            </label>
            <div className="text-gray-900 bg-gray-50 px-4 py-2 rounded-lg">
              {userInfo.email}
            </div>
            <p className="text-sm text-gray-500 mt-1">
              이메일은 변경할 수 없습니다.
            </p>
          </div>

          {/* 닉네임 정보 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              닉네임
            </label>
            {isEditing ? (
              <div className="space-y-4">
                <input
                  type="text"
                  value={newNickname}
                  onChange={(e) => setNewNickname(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent placeholder-gray-400"
                  placeholder="새로운 닉네임을 입력하세요"
                  maxLength={20}
                />
                <div className="flex space-x-3">
                  <button
                    onClick={handleCancel}
                    className="flex-1 px-4 py-2 text-sm font-medium bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                  >
                    취소
                  </button>
                  <button
                    onClick={handleNicknameChange}
                    className="flex-1 px-4 py-2 text-sm font-medium bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors"
                  >
                    변경
                  </button>
                </div>
              </div>
            ) : (
              <div className="flex items-center justify-between">
                <div className="text-gray-900 bg-gray-50 px-4 py-2 rounded-lg flex-1">
                  {userInfo.nickname}
                </div>
                <button
                  onClick={() => setIsEditing(true)}
                  className="ml-2 px-4 py-2 text-sm font-medium bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors"
                >
                  수정
                </button>
              </div>
            )}
          </div>
        </div>
      </section>

      {/* 계정 정보 섹션 */}
      <section className="bg-white p-8 rounded-xl border border-gray-200">
        <h2 className="text-xl font-semibold mb-6 text-gray-800">계정 정보</h2>
        <div className="space-y-4">
          <div className="flex items-center justify-between py-3 border-b border-gray-100 last:border-b-0">
            <span className="text-gray-600">소셜 로그인</span>
            <span className="text-gray-900 font-medium">{userInfo.social}</span>
          </div>
          <div className="flex items-center justify-between py-3 border-b border-gray-100">
            <span className="text-gray-600">권한</span>
            <span className="text-gray-900 font-medium">
              {userInfo.authority}
            </span>
          </div>
          <div className="flex items-center justify-between py-3 border-b border-gray-100 last:border-b-0">
            <span className="text-gray-600">가입일</span>
            <span className="text-gray-900 font-medium">
              {userInfo.createdAt
                ? formatKoreanDateTime(userInfo.createdAt)
                : "정보 없음"}
            </span>
          </div>
        </div>
      </section>

      {/* 내가 작성한 게시글 섹션 */}
      <section className="bg-white p-8 rounded-xl border border-gray-200 mt-6">
        <h2 className="text-xl font-semibold mb-6 text-gray-800 flex items-center gap-2">
          <BiMessageSquareDetail
            className="text-2xl text-gray-700"
            aria-hidden="true"
          />
          내가 작성한 게시글
        </h2>

        {/* 게시글 목록 */}
        <div aria-label="게시글 목록">
          {isLoadingBoards ? (
            <LoadingSpinner />
          ) : myBoards.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-8 px-4 bg-gray-50 rounded-lg border border-gray-200">
              <div className="text-gray-500 font-medium">
                작성한 게시글이 없습니다.
              </div>
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
                      <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-36">
                        글쓴이
                      </th>
                      <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-30">
                        작성일
                      </th>
                      <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-20">
                        조회
                      </th>
                      <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-20">
                        추천
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {myBoards.map((board) => (
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
                            {typeof board.commentsCount === "number" &&
                              board.commentsCount > 0 && (
                                <span className="text-xs text-gray-400 font-semibold">
                                  [{board.commentsCount}]
                                </span>
                              )}
                          </div>
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-500 w-36 text-center">
                          {board.nickname}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-500 w-24 text-center">
                          {formatRelativeTime(board.createdAt)}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-500 w-10 text-center">
                          {board.view?.toLocaleString() || 0}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-500 w-10 text-center">
                          {board.likes?.toLocaleString() || 0}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* 모바일 카드 목록 */}
              <div className="md:hidden">
                {myBoards.map((board) => (
                  <article
                    key={board.boardId}
                    onClick={() => handleBoardClick(board.boardId)}
                    className="bg-white border-b border-gray-200 py-3 hover:bg-gray-50 cursor-pointer transition-colors last:border-b-0"
                  >
                    <h3 className="text-sm font-medium text-gray-900 mb-1">
                      <div className="flex items-center gap-1 min-w-0">
                        {board.hasImage ? (
                          <BiImage className="text-green-600 flex-shrink-0 text-sm" />
                        ) : (
                          <BiFile className="text-gray-400 flex-shrink-0 text-sm" />
                        )}
                        <span className="truncate">{board.title}</span>
                        {typeof board.commentsCount === "number" &&
                          board.commentsCount > 0 && (
                            <span className="text-xs text-gray-400 font-semibold">
                              [{board.commentsCount}]
                            </span>
                          )}
                      </div>
                    </h3>
                    <div className="flex items-center text-xs text-gray-500 space-x-2">
                      <span>{board.nickname}</span>
                      <span className="text-gray-300">|</span>
                      <span>{formatRelativeTime(board.createdAt)}</span>
                      <span className="text-gray-300">|</span>
                      <span>조회 {board.view?.toLocaleString() || 0}</span>
                      <span className="text-gray-300">|</span>
                      <span>추천 {board.likes?.toLocaleString() || 0}</span>
                    </div>
                  </article>
                ))}
              </div>
            </>
          )}
        </div>

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
      </section>
    </main>
  );
}

export default MyPage;
