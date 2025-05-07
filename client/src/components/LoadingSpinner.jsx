import React from "react";
import { AiOutlineLoading } from "react-icons/ai";

function LoadingSpinner({ size = "4xl", color = "gray-700" }) {
  return (
    <div className="flex justify-center items-center min-h-[400px]">
      <AiOutlineLoading className={`animate-spin text-${size} text-${color}`} />
    </div>
  );
}

export default LoadingSpinner;
