import "@toast-ui/editor/dist/toastui-editor.css";
import { Editor } from "@toast-ui/react-editor";
import axios from "axios";
import React, { useRef, useState } from "react";
import { BiPencil } from "react-icons/bi";
import { useNavigate, useSearchParams } from "react-router-dom";

const DictionaryWritePage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const type = searchParams.get("type");
  const isRunnerDictionary = type === "runner";
  const editorRef = useRef();

  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");

  const handleSubmit = async () => {
    try {
      const content = editorRef.current.getInstance().getMarkdown();
      const response = await axios.post(
        "http://localhost:8080/api/v1/dictionaries",
        {
          title,
          category: isRunnerDictionary ? "person" : "guild",
          author,
          content,
        }
      );

      if (response.status === 204) {
        navigate(-1);
      }
    } catch (error) {
      console.error("사전 작성 중 오류가 발생했습니다:", error);
    }
  };

  return (
    <div>
      <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
        <BiPencil className="text-2xl text-gray-700" />
        {isRunnerDictionary ? "런너 사전 작성" : "길드 사전 작성"}
      </h2>
      <div className="space-y-4">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="w-full md:flex-1">
            <label
              htmlFor="title"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              {isRunnerDictionary ? "닉네임" : "길드명"}
            </label>
            <input
              type="text"
              id="title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400"
              placeholder={
                isRunnerDictionary
                  ? "닉네임을 입력하세요"
                  : "길드명을 입력하세요"
              }
            />
          </div>
          <div className="w-full md:w-1/3">
            <label
              htmlFor="author"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              작성자
            </label>
            <input
              type="text"
              id="author"
              value={author}
              onChange={(e) => setAuthor(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400"
              placeholder="작성자를 입력하세요"
            />
          </div>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            내용
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
            />
          </div>
          <div className="flex justify-end gap-2 mt-8">
            <button
              onClick={() => navigate(-1)}
              className="px-4 py-2 text-sm font-medium bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors shadow-sm"
            >
              뒤로가기
            </button>
            <button
              onClick={handleSubmit}
              className="px-4 py-2 text-sm font-medium bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors shadow-sm"
            >
              작성하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DictionaryWritePage;
