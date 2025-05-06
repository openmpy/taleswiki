import "@toast-ui/editor/dist/toastui-editor.css";
import { Editor } from "@toast-ui/react-editor";
import axios from "axios";
import React, { useEffect, useRef, useState } from "react";
import { BiPencil } from "react-icons/bi";
import { useNavigate, useParams } from "react-router-dom";

const DictionaryEditPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const editorRef = useRef();
  const [dictionary, setDictionary] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [author, setAuthor] = useState("");
  const [dictionaryId, setDictionaryId] = useState(null);

  useEffect(() => {
    const fetchDictionary = async () => {
      setIsLoading(true);
      try {
        const response = await fetch(
          `http://localhost:8080/api/v1/dictionaries/histories/${id}`
        );
        const data = await response.json();

        setDictionary(data);
        setDictionaryId(data.dictionaryId);
        if (editorRef.current) {
          editorRef.current.getInstance().setMarkdown(data.content || "");
        }
      } catch (error) {
        console.error("사전 정보를 가져오는데 실패했습니다:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDictionary();
  }, [id]);

  const handleSubmit = async () => {
    try {
      const content = editorRef.current.getInstance().getMarkdown();
      const response = await axios.put(
        `http://localhost:8080/api/v1/dictionaries/${dictionaryId}`,
        {
          author,
          content,
        }
      );

      if (response.status === 200) {
        navigate(`/dictionary/${response.data.dictionaryHistoryId}`);
      }
    } catch (error) {
      console.error("사전 편집 중 오류가 발생했습니다:", error);
    }
  };

  if (isLoading) {
    return <div>로딩 중...</div>;
  }

  if (!dictionary) {
    return <div>사전 정보를 찾을 수 없습니다.</div>;
  }

  return (
    <div>
      <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
        <BiPencil className="text-2xl text-gray-700" />
        사전 편집
      </h2>
      <div className="space-y-4">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="w-full md:flex-1">
            <label
              htmlFor="title"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              제목
            </label>
            <input
              type="text"
              id="title"
              value={dictionary.title}
              disabled
              className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-500"
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
              initialValue={dictionary.content || ""}
              previewStyle="vertical"
              height="500px"
              initialEditType="markdown"
              useCommandShortcut={true}
              language="ko-KR"
            />
          </div>
          <div className="flex flex-col md:flex-row justify-end gap-2 mt-8">
            <button
              onClick={() => navigate(-1)}
              className="px-4 py-2 text-sm font-medium bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              뒤로가기
            </button>
            <button
              onClick={handleSubmit}
              className="px-4 py-2 text-sm font-medium bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition-colors"
            >
              수정하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DictionaryEditPage;
