import { Client } from "@stomp/stompjs";
import React, { useEffect, useRef, useState } from "react";
import { HiOutlineChat } from "react-icons/hi";
import { IoChevronDown, IoChevronUp, IoClose, IoSend } from "react-icons/io5";
import SockJS from "sockjs-client";
import axiosInstance from "../utils/axiosConfig";
import { formatKoreanDateTime } from "../utils/dateUtils";
import { getNickname } from "../utils/nickname";

const ChatRoom = ({ onExpandChange }) => {
  const [isConnected, setIsConnected] = useState(false);
  const [stompClient, setStompClient] = useState(null);
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([]);
  const [isExpanded, setIsExpanded] = useState(window.innerWidth >= 768);
  const [isManuallyExpanded, setIsManuallyExpanded] = useState(false);
  const [isComposing, setIsComposing] = useState(false);
  const chatContainerRef = useRef(null);

  const getSessionId = () => {
    const storedSessionId = localStorage.getItem("chat-session-id");
    if (storedSessionId) {
      return storedSessionId;
    }
    const newSessionId = crypto.randomUUID();
    localStorage.setItem("chat-session-id", newSessionId);
    return newSessionId;
  };

  const sessionId = useRef(getSessionId());
  const nickname = useRef(getNickname());

  const scrollToBottom = () => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop =
        chatContainerRef.current.scrollHeight;
    }
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    const handleResize = () => {
      if (!isManuallyExpanded) {
        setIsExpanded(window.innerWidth >= 768);
        onExpandChange?.(window.innerWidth >= 768);
      }
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [isManuallyExpanded, onExpandChange]);

  const fetchMessages = async () => {
    try {
      const { data } = await axiosInstance.get("/api/v1/chats");
      setMessages(data.messages);
    } catch (error) {
      console.error("채팅 메시지를 불러오는데 실패했습니다:", error);
    }
  };

  useEffect(() => {
    fetchMessages();
  }, []);

  useEffect(() => {
    const socket = new SockJS(`${import.meta.env.VITE_API_URL}/ws`);
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        setIsConnected(true);
        client.subscribe("/topic/public", (message) => {
          const receivedMessage = JSON.parse(message.body);
          setMessages((prev) => [...prev, receivedMessage]);
        });

        client.subscribe("/user/queue/errors", (errorFrame) => {
          const error = JSON.parse(errorFrame.body);
          alert(`${error.message || "알 수 없는 에러가 발생했습니다."}`);
        });
      },
      onDisconnect: () => {
        setIsConnected(false);
      },
    });

    client.activate();
    setStompClient(client);

    return () => {
      if (client) {
        client.deactivate();
      }
    };
  }, []);

  const handleSendMessage = () => {
    if (message.trim() && stompClient) {
      stompClient.publish({
        destination: "/app/chat",
        body: JSON.stringify({
          message: message.trim(),
          sessionId: sessionId.current,
          nickname: nickname.current,
        }),
      });
      setMessage("");
      scrollToBottom();
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter" && !e.shiftKey && !isComposing) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleCompositionStart = () => {
    setIsComposing(true);
  };

  const handleCompositionEnd = () => {
    setIsComposing(false);
  };

  return (
    <div
      className={`bg-white md:rounded-lg rounded-none border border-gray-200 p-4 ${
        isExpanded
          ? window.innerWidth >= 768
            ? "h-[600px]"
            : "h-[500px]"
          : "h-auto"
      } flex flex-col`}
    >
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold flex items-center gap-2 text-gray-700">
          <HiOutlineChat className="text-gray-600" />
          채팅방
          <div
            className={`w-2 h-2 rounded-full ${
              isConnected ? "bg-green-500" : "bg-red-500"
            }`}
          />
        </h3>
        <button
          onClick={() => {
            setIsManuallyExpanded(true);
            setIsExpanded(!isExpanded);
            onExpandChange?.(!isExpanded);
          }}
          className="text-gray-600 hover:text-gray-800 transition-colors"
        >
          {window.innerWidth >= 768 ? (
            <IoClose size={20} />
          ) : isExpanded ? (
            <IoChevronDown size={20} />
          ) : (
            <IoChevronUp size={20} />
          )}
        </button>
      </div>
      {isExpanded && (
        <div className="flex-1 flex flex-col min-h-0">
          {/* 채팅 메시지 목록 */}
          <div
            ref={chatContainerRef}
            className="flex-1 bg-gray-50 rounded-lg p-4 mt-2 mb-4 overflow-y-auto"
          >
            {messages.map((msg, index) => (
              <div key={index} className="mb-4">
                <div
                  className={`flex items-start gap-2 ${
                    msg.sessionId === sessionId.current ? "justify-end" : ""
                  }`}
                >
                  <div className="flex-1">
                    <div
                      className={`flex flex-col gap-1 ${
                        msg.sessionId === sessionId.current ? "items-end" : ""
                      }`}
                    >
                      <span
                        className={`text-xs ${
                          msg.sessionId === sessionId.current
                            ? "text-gray-700 font-semibold"
                            : "text-gray-500"
                        }`}
                      >
                        {msg.nickname}
                      </span>
                      <div
                        className={`flex items-end gap-1 ${
                          msg.sessionId === sessionId.current
                            ? "flex-row-reverse"
                            : ""
                        }`}
                      >
                        <div
                          className={`rounded px-3 py-2 max-w-[280px] border ${
                            msg.sessionId === sessionId.current
                              ? "bg-gray-700 text-white border-gray-700"
                              : "bg-white text-gray-700 border-gray-200"
                          }`}
                        >
                          <span className="text-sm break-words whitespace-pre-wrap inline-block w-full">
                            {msg.message}
                          </span>
                          <div className="text-xs text-gray-400 mt-1">
                            {formatKoreanDateTime(msg.createdAt)}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* 채팅 입력 폼 */}
          <div className="mt-auto">
            <div className="flex items-center gap-2">
              <input
                type="text"
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                onKeyDown={handleKeyPress}
                onCompositionStart={handleCompositionStart}
                onCompositionEnd={handleCompositionEnd}
                placeholder="메시지를 입력해주세요"
                className="flex-1 h-8 px-2 border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 placeholder:text-gray-400"
              />
              <button
                type="button"
                onClick={handleSendMessage}
                className="h-8 px-2 bg-gray-700 hover:bg-gray-600 text-white rounded transition-colors duration-200 flex items-center justify-center"
              >
                <IoSend className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatRoom;
