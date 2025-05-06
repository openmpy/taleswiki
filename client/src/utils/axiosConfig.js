import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

// 요청 인터셉터
axiosInstance.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      const { status } = error.response;
      const errorMessages = {
        401: "인증이 필요합니다.",
        403: "접근 권한이 없습니다.",
        404: "요청한 리소스를 찾을 수 없습니다.",
        500: "서버 오류가 발생했습니다.",
      };

      const message =
        errorMessages[status] || "알 수 없는 오류가 발생했습니다.";
      console.error(message);

      if (status === 403) {
        window.location.href = "/blocked";
      }
    } else if (error.request) {
      console.error("서버로부터 응답이 없습니다.");
    } else {
      console.error("요청 설정 중 오류가 발생했습니다.");
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
