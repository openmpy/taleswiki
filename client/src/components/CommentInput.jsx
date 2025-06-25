import React, { useState } from "react";
import axiosInstance from "../utils/axiosConfig";

const CommentInput = ({ boardId, onCommentAdded, parentId = null }) => {
  const [content, setContent] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) {
      alert("댓글 내용을 입력해주세요.");
      return;
    }
    setIsSubmitting(true);
    try {
      await axiosInstance.post(`/api/v1/boards/comments/${boardId}`, {
        content,
        parentId: parentId,
      });
      setContent("");
      if (onCommentAdded) onCommentAdded();
    } catch (error) {
      if (error.response && error.response.status === 401) {
        alert("로그인이 필요합니다.");
      } else {
        alert("댓글 등록에 실패했습니다.");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="w-full flex flex-col gap-2 my-4">
      <textarea
        className="w-full border border-gray-300 rounded p-2 resize-none min-h-[60px] focus:outline-none focus:ring-1 focus:ring-blue-300 text-sm placeholder:text-gray-400"
        placeholder={parentId ? "답글을 입력해주세요." : "댓글을 입력해주세요."}
        value={content}
        onChange={(e) => setContent(e.target.value)}
        disabled={isSubmitting}
        maxLength={500}
      />
      <button
        type="submit"
        className="w-full px-4 py-2 text-sm font-medium rounded-lg transition-colors bg-gray-700 text-white hover:bg-gray-800 disabled:bg-gray-300 disabled:text-gray-500"
        disabled={isSubmitting}
      >
        {isSubmitting
          ? parentId
            ? "답글 등록 중..."
            : "등록 중..."
          : parentId
          ? "답글 등록"
          : "댓글 등록"}
      </button>
    </form>
  );
};

export default CommentInput;
