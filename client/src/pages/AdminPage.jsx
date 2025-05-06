import axios from "axios";
import { useEffect, useState } from "react";
import { FaUserShield } from "react-icons/fa";
import AdminLogin from "../components/admin/AdminLogin";
import DictionaryTable from "../components/admin/DictionaryTable";
import Pagination from "../components/admin/Pagination";

function AdminPage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [dictionaries, setDictionaries] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

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

    fetchDictionaries();
  }, [isAuthenticated, currentPage]);

  const handleStatusChange = async (dictionaryId, newStatus) => {
    try {
      await axios.patch(
        `http://localhost:8080/api/v1/dictionaries/${dictionaryId}?status=${newStatus}`,
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
        `http://localhost:8080/api/v1/dictionaries/${dictionaryId}`,
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

  if (!isAuthenticated) {
    return <AdminLogin onLoginSuccess={() => setIsAuthenticated(true)} />;
  }

  return (
    <div>
      <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
        <FaUserShield className="text-blue-600" />
        관리자 페이지
      </h2>

      {isLoading ? (
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      ) : (
        <>
          <DictionaryTable
            dictionaries={dictionaries}
            onStatusChange={handleStatusChange}
            onDelete={handleDelete}
          />
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
