import axios from "axios";
import { useEffect, useState } from "react";
import { AiOutlineLoading } from "react-icons/ai";
import { FaUserShield } from "react-icons/fa";
import AdminLogin from "../components/admin/AdminLogin";
import DictionaryHistoryTable from "../components/admin/DictionaryHistoryTable";
import DictionaryTable from "../components/admin/DictionaryTable";
import Pagination from "../components/admin/Pagination";

function AdminPage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [dictionaries, setDictionaries] = useState([]);
  const [histories, setHistories] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [activeTab, setActiveTab] = useState("dictionaries"); // "dictionaries" or "histories"

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/admin/me",
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          }
        );

        if (response.status === 204) {
          setIsAuthenticated(true);
        } else {
          setIsAuthenticated(false);
        }
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
        const response = await axios.get(
          `http://localhost:8080/api/v1/admin/dictionaries?page=${currentPage}&size=100`,
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          }
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
        const response = await axios.get(
          `http://localhost:8080/api/v1/admin/dictionaries/histories?page=${currentPage}&size=100`,
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          }
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

  const handleStatusChange = async (dictionaryId, newStatus) => {
    try {
      await axios.patch(
        `http://localhost:8080/api/v1/admin/dictionaries/${dictionaryId}?status=${newStatus}`,
        {},
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      // 상태 변경 후 목록 새로고침
      const response = await axios.get(
        `http://localhost:8080/api/v1/admin/dictionaries?page=${currentPage}&size=100`,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
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
      await axios.delete(
        `http://localhost:8080/api/v1/admin/dictionaries/${dictionaryId}`,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      // 삭제 후 목록 새로고침
      const response = await axios.get(
        `http://localhost:8080/api/v1/admin/dictionaries?page=${currentPage}&size=100`,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
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
      await axios.patch(
        `http://localhost:8080/api/v1/admin/dictionaries/histories/${dictionaryHistoryId}?status=${newStatus}`,
        {},
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      // 상태 변경 후 목록 새로고침
      const response = await axios.get(
        `http://localhost:8080/api/v1/admin/dictionaries/histories?page=${currentPage}&size=100`,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );
      setHistories(response.data.content);
    } catch (error) {
      console.error("상태 변경 중 오류 발생:", error);
      alert("상태 변경에 실패했습니다.");
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

      <div className="mb-4">
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
          className={`px-3 py-1.5 text-sm rounded ${
            activeTab === "histories"
              ? "bg-gray-600 text-white"
              : "bg-gray-100 text-gray-600"
          }`}
          onClick={() => setActiveTab("histories")}
        >
          사전 기록
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
          ) : (
            <DictionaryHistoryTable
              histories={histories}
              onStatusChange={handleHistoryStatusChange}
            />
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
