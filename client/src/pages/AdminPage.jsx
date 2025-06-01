import { useEffect, useState } from "react";
import { AiOutlineLoading } from "react-icons/ai";
import { FaSync, FaUserShield } from "react-icons/fa";
import AdminLogin from "../components/admin/AdminLogin";
import BlacklistTable from "../components/admin/BlacklistTable";
import ChatTable from "../components/admin/ChatTable";
import DictionaryHistoryTable from "../components/admin/DictionaryHistoryTable";
import DictionaryTable from "../components/admin/DictionaryTable";
import Pagination from "../components/admin/Pagination";
import axiosInstance from "../utils/axiosConfig";

function AdminPage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [dictionaries, setDictionaries] = useState([]);
  const [histories, setHistories] = useState([]);
  const [blacklists, setBlacklists] = useState([]);
  const [chats, setChats] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [isSyncing, setIsSyncing] = useState(false);
  const [activeTab, setActiveTab] = useState("dictionaries"); // "dictionaries", "histories", "blacklists", or "chats"
  const [newBlacklist, setNewBlacklist] = useState({ ip: "", reason: "" });

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await axiosInstance.get("/api/v1/admin/me");
        setIsAuthenticated(response.status === 204);
      } catch (error) {
        console.error("인증 확인 중 오류 발생:", error);
        setIsAuthenticated(false);
      }
    };

    checkAuth();
  }, []);

  useEffect(() => {
    const fetchDictionaries = async () => {
      if (!isAuthenticated) return;

      setIsLoading(true);
      try {
        const response = await axiosInstance.get(
          `/api/v1/admin/dictionaries?page=${currentPage}&size=100`
        );
        setDictionaries(response.data.content);
        setTotalPages(response.data.totalPages);
      } catch (error) {
        console.error("사전 목록을 가져오는 중 오류 발생:", error);
      } finally {
        setIsLoading(false);
      }
    };

    if (activeTab === "dictionaries") {
      fetchDictionaries();
    }
  }, [isAuthenticated, currentPage, activeTab]);

  useEffect(() => {
    const fetchHistories = async () => {
      if (!isAuthenticated) return;

      setIsLoading(true);
      try {
        const response = await axiosInstance.get(
          `/api/v1/admin/dictionaries/histories?page=${currentPage}&size=100`
        );
        setHistories(response.data.content);
        setTotalPages(response.data.totalPages);
      } catch (error) {
        console.error("사전 기록 목록을 가져오는 중 오류 발생:", error);
      } finally {
        setIsLoading(false);
      }
    };

    if (activeTab === "histories") {
      fetchHistories();
    }
  }, [isAuthenticated, currentPage, activeTab]);

  useEffect(() => {
    const fetchBlacklists = async () => {
      if (!isAuthenticated) return;

      setIsLoading(true);
      try {
        const response = await axiosInstance.get(
          `/api/v1/admin/blacklist?page=${currentPage}&size=100`
        );
        setBlacklists(response.data.content);
        setTotalPages(response.data.totalPages);
      } catch (error) {
        console.error("블랙리스트 목록을 가져오는 중 오류 발생:", error);
      } finally {
        setIsLoading(false);
      }
    };

    if (activeTab === "blacklists") {
      fetchBlacklists();
    }
  }, [isAuthenticated, currentPage, activeTab]);

  useEffect(() => {
    const fetchChats = async () => {
      if (!isAuthenticated) return;

      setIsLoading(true);
      try {
        const response = await axiosInstance.get(
          `/api/v1/admin/chats?page=${currentPage}&size=100`
        );
        setChats(response.data.content);
        setTotalPages(response.data.totalPages);
      } catch (error) {
        console.error("채팅 기록을 가져오는 중 오류 발생:", error);
      } finally {
        setIsLoading(false);
      }
    };

    if (activeTab === "chats") {
      fetchChats();
    }
  }, [isAuthenticated, currentPage, activeTab]);

  const handleStatusChange = async (dictionaryId, newStatus) => {
    try {
      await axiosInstance.patch(
        `/api/v1/admin/dictionaries/${dictionaryId}?status=${newStatus}`
      );

      const response = await axiosInstance.get(
        `/api/v1/admin/dictionaries?page=${currentPage}&size=100`
      );
      setDictionaries(response.data.content);
    } catch (error) {
      console.error("상태 변경 중 오류 발생:", error);
      alert("상태 변경에 실패했습니다.");
    }
  };

  const handleDelete = async (dictionaryId) => {
    if (!window.confirm("정말로 이 사전을 삭제하시겠습니까?")) {
      return;
    }

    try {
      await axiosInstance.delete(`/api/v1/admin/dictionaries/${dictionaryId}`);

      const response = await axiosInstance.get(
        `/api/v1/admin/dictionaries?page=${currentPage}&size=100`
      );
      setDictionaries(response.data.content);
      alert("사전이 성공적으로 삭제되었습니다.");
    } catch (error) {
      console.error("사전 삭제 중 오류 발생:", error);
      alert("사전 삭제에 실패했습니다.");
    }
  };

  const handleHistoryStatusChange = async (dictionaryHistoryId, newStatus) => {
    try {
      await axiosInstance.patch(
        `/api/v1/admin/dictionaries/histories/${dictionaryHistoryId}?status=${newStatus}`
      );

      const response = await axiosInstance.get(
        `/api/v1/admin/dictionaries/histories?page=${currentPage}&size=100`
      );
      setHistories(response.data.content);
    } catch (error) {
      console.error("상태 변경 중 오류 발생:", error);
      alert("상태 변경에 실패했습니다.");
    }
  };

  const handleBlacklistDelete = async (blacklistId) => {
    if (!window.confirm("정말로 이 IP를 블랙리스트에서 제거하시겠습니까?")) {
      return;
    }

    try {
      await axiosInstance.delete(`/api/v1/admin/blacklist/${blacklistId}`);

      const response = await axiosInstance.get(
        `/api/v1/admin/blacklist?page=${currentPage}&size=100`
      );
      setBlacklists(response.data.content);
      alert("블랙리스트에서 성공적으로 제거되었습니다.");
    } catch (error) {
      console.error("블랙리스트 제거 중 오류 발생:", error);
      alert("블랙리스트 제거에 실패했습니다.");
    }
  };

  const handleChatDelete = async (chatId) => {
    try {
      await axiosInstance.delete(`/api/v1/admin/chats/${chatId}`);

      const response = await axiosInstance.get(
        `/api/v1/admin/chats?page=${currentPage}&size=100`
      );
      setChats(response.data.content);
    } catch (error) {
      console.error("채팅 삭제 중 오류 발생:", error);
      alert("채팅 삭제에 실패했습니다.");
    }
  };

  const handleAddBlacklist = async (e) => {
    e.preventDefault();

    try {
      await axiosInstance.post("/api/v1/admin/blacklist", newBlacklist);

      const response = await axiosInstance.get(
        `/api/v1/admin/blacklist?page=${currentPage}&size=100`
      );
      setBlacklists(response.data.content);
      setNewBlacklist({ ip: "", reason: "" });
    } catch (error) {
      alert(error.response.data.message);
    }
  };

  const handleSyncSearchHistory = async () => {
    if (!window.confirm("검색 기록을 동기화하시겠습니까?")) {
      return;
    }

    setIsSyncing(true);
    try {
      await axiosInstance.post("/api/v1/admin/sync/dictionary-search");
      alert("검색 기록 동기화가 완료되었습니다.");
    } catch (error) {
      console.error("검색 기록 동기화 중 오류 발생:", error);
      alert("검색 기록 동기화에 실패했습니다.");
    } finally {
      setIsSyncing(false);
    }
  };

  if (!isAuthenticated) {
    return <AdminLogin onLoginSuccess={() => setIsAuthenticated(true)} />;
  }

  return (
    <div>
      <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
        <FaUserShield className="text-blue-600" />
        관리자 페이지
      </h2>

      <div className="mb-4 flex justify-between items-center">
        <div>
          <button
            className={`px-3 py-1.5 mr-2 text-sm rounded ${
              activeTab === "dictionaries"
                ? "bg-gray-600 text-white"
                : "bg-gray-100 text-gray-600"
            }`}
            onClick={() => setActiveTab("dictionaries")}
          >
            사전 목록
          </button>
          <button
            className={`px-3 py-1.5 mr-2 text-sm rounded ${
              activeTab === "histories"
                ? "bg-gray-600 text-white"
                : "bg-gray-100 text-gray-600"
            }`}
            onClick={() => setActiveTab("histories")}
          >
            사전 기록
          </button>
          <button
            className={`px-3 py-1.5 mr-2 text-sm rounded ${
              activeTab === "blacklists"
                ? "bg-gray-600 text-white"
                : "bg-gray-100 text-gray-600"
            }`}
            onClick={() => setActiveTab("blacklists")}
          >
            블랙리스트
          </button>
          <button
            className={`px-3 py-1.5 text-sm rounded ${
              activeTab === "chats"
                ? "bg-gray-600 text-white"
                : "bg-gray-100 text-gray-600"
            }`}
            onClick={() => setActiveTab("chats")}
          >
            채팅 기록
          </button>
        </div>
        <button
          onClick={handleSyncSearchHistory}
          disabled={isSyncing}
          className={`px-3 py-1.5 text-sm rounded flex items-center gap-2 ${
            isSyncing
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-gray-600 hover:bg-gray-700"
          } text-white`}
        >
          <FaSync className={isSyncing ? "animate-spin" : ""} />
          {isSyncing ? "동기화 중..." : "검색 기록 동기화"}
        </button>
      </div>

      {isLoading ? (
        <div className="flex justify-center items-center min-h-[400px]">
          <AiOutlineLoading className="animate-spin text-4xl text-gray-700" />
        </div>
      ) : (
        <>
          {activeTab === "dictionaries" ? (
            <DictionaryTable
              dictionaries={dictionaries}
              onStatusChange={handleStatusChange}
              onDelete={handleDelete}
            />
          ) : activeTab === "histories" ? (
            <DictionaryHistoryTable
              histories={histories}
              onStatusChange={handleHistoryStatusChange}
            />
          ) : activeTab === "blacklists" ? (
            <>
              <form onSubmit={handleAddBlacklist} className="mb-4">
                <div className="flex flex-col sm:flex-row gap-3">
                  <input
                    type="text"
                    placeholder="192.168.0.1"
                    value={newBlacklist.ip}
                    onChange={(e) =>
                      setNewBlacklist({ ...newBlacklist, ip: e.target.value })
                    }
                    className="w-full sm:flex-1 px-2 py-1.5 border border-gray-200 rounded text-sm placeholder:text-gray-400"
                  />
                  <input
                    type="text"
                    placeholder="사유"
                    value={newBlacklist.reason}
                    onChange={(e) =>
                      setNewBlacklist({
                        ...newBlacklist,
                        reason: e.target.value,
                      })
                    }
                    className="w-full sm:flex-1 px-2 py-1.5 border border-gray-200 rounded text-sm placeholder:text-gray-400"
                  />
                  <button
                    type="submit"
                    className="w-full sm:w-auto px-3 py-1.5 bg-gray-600 text-white rounded text-sm hover:bg-gray-700"
                  >
                    추가
                  </button>
                </div>
              </form>
              <BlacklistTable
                blacklists={blacklists}
                onDelete={handleBlacklistDelete}
              />
            </>
          ) : (
            <ChatTable chats={chats} onDelete={handleChatDelete} />
          )}
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        </>
      )}
    </div>
  );
}

export default AdminPage;
