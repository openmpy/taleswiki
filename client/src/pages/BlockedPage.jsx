import React from "react";

const BlockedPage = () => {
  return (
    <div className="fixed inset-0 bg-white flex items-center justify-center">
      <div className="text-center max-w-md mx-auto p-8">
        <h1 className="text-4xl font-bold text-red-600 mb-4">접근 제한</h1>
        <p className="text-gray-600 mb-6">
          현재 귀하의 IP는 접근이 차단되었습니다.
          <br />
          문의: test@test.com
        </p>
      </div>
    </div>
  );
};

export default BlockedPage;
