import axios from "axios";
import { useEffect, useState } from "react";
import { FaUserShield } from "react-icons/fa";
import AdminLogin from "../components/admin/AdminLogin";

function AdminPage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

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

  if (!isAuthenticated) {
    return <AdminLogin onLoginSuccess={() => setIsAuthenticated(true)} />;
  }

  return (
    <>
      <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
        <FaUserShield className="text-blue-600" />
        관리자 페이지
      </h2>
    </>
  );
}

export default AdminPage;
