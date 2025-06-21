import "@toast-ui/editor/dist/toastui-editor.css";
import { Editor } from "@toast-ui/react-editor";
import React, { useRef, useState } from "react";
import { AiOutlineLoading } from "react-icons/ai";
import { BiPencil } from "react-icons/bi";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../utils/axiosConfig";

const BoardWritePage = () => {
  const navigate = useNavigate();
  const editorRef = useRef();

  const [title, setTitle] = useState("");
  const [isUploading, setIsUploading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleImageUpload = async (file) => {
    try {
      setIsUploading(true);
      const formData = new FormData();
      formData.append("image", file);

      const response = await axiosInstance.post(
        "/api/v1/images/upload",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      const imageUrl = `${import.meta.env.VITE_R2_API_URL}/images/tmp/${
        response.data.fileName
      }`;
      return imageUrl;
    } catch (error) {
      if (error.response?.status === 401) {
        alert("로그인이 필요합니다.");
        navigate("/login");
        return null;
      }
      console.error("이미지 업로드 실패:", error);
      alert("이미지 업로드에 실패했습니다.");
      return null;
    } finally {
      setIsUploading(false);
    }
  };

  const handleSubmit = async () => {
    try {
      setIsSubmitting(true);
      const content = editorRef.current.getInstance().getMarkdown();

      const response = await axiosInstance.post("/api/v1/boards", {
        title,
        content,
      });

      if (response.status === 201) {
        navigate(`/community`);
      }
    } catch (error) {
      if (error.response?.status === 401) {
        alert("로그인이 필요합니다.");
        navigate("/login");
        return;
      }
      if (error.response?.status === 400) {
        alert(error.response.data.message);
        return;
      }
      alert("게시글 작성에 실패했습니다.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main>
      <header>
        <h1 className="text-xl font-semibold mb-4 flex items-center gap-2">
          <BiPencil className="text-2xl text-gray-700" aria-hidden="true" />
          게시글 작성
        </h1>
        <p className="text-sm font-bold text-red-500 mb-4">
          * 부정 이용 방지를 위해 IP 정보가 수집됩니다.
        </p>
      </header>

      {(isUploading || isSubmitting) && (
        <div
          className="fixed inset-0 backdrop-blur-sm bg-white/30 flex items-center justify-center z-50"
          role="alert"
          aria-busy="true"
        >
          <div className="bg-white/80 backdrop-blur-md p-8 rounded-lg border border-gray-200 flex flex-col items-center gap-4">
            <AiOutlineLoading
              className="animate-spin text-4xl text-gray-700"
              aria-hidden="true"
            />
            <p className="text-base text-gray-700">
              {isUploading ? "이미지 업로드 중..." : "게시글 작성 중..."}
            </p>
          </div>
        </div>
      )}

      <form
        className="space-y-4"
        onSubmit={(e) => {
          e.preventDefault();
          handleSubmit();
        }}
      >
        <div>
          <label
            htmlFor="title"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            제목
          </label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400"
            placeholder="제목을 입력하세요"
            required
            aria-required="true"
          />
        </div>

        <div>
          <label className="text-sm font-medium text-gray-700 mb-1 flex items-center gap-1">
            내용
            <span className="text-gray-500 text-xs">(마크다운 문법 지원)</span>
          </label>
          <div>
            <Editor
              ref={editorRef}
              initialValue=""
              previewStyle="vertical"
              height="500px"
              initialEditType="markdown"
              useCommandShortcut={true}
              language="ko-KR"
              hooks={{
                addImageBlobHook: async (blob, callback) => {
                  const url = await handleImageUpload(blob);
                  if (url) {
                    callback(url);
                  }
                },
              }}
            />
          </div>

          <nav className="flex flex-col md:flex-row justify-end gap-2 mt-8">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="px-4 py-2 text-sm font-medium bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              뒤로가기
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-sm font-medium bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors"
            >
              작성하기
            </button>
          </nav>
        </div>
      </form>
    </main>
  );
};

export default BoardWritePage;
