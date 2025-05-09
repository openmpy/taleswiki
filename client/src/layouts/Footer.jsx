import React from "react";
import { FaDiscord, FaEnvelope } from "react-icons/fa";
import { SiKakaotalk } from "react-icons/si";

function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-gray-800 text-white p-3 text-center mt-auto border-t border-gray-700 text-sm">
      <p className="mb-2">
        &copy; {currentYear} 테일즈위키. All rights reserved.
      </p>
      <div className="flex justify-center items-center space-x-3">
        <a
          href="https://discord.gg/eXEtzDEk"
          target="_blank"
          rel="noopener noreferrer"
          className="hover:text-blue-400"
          aria-label="Discord 서버로 이동"
        >
          <FaDiscord size={18} />
        </a>
        <a
          href="https://open.kakao.com/o/gbPFwsvh"
          target="_blank"
          rel="noopener noreferrer"
          className="hover:text-blue-400"
          aria-label="카카오톡 오픈채팅으로 이동"
        >
          <SiKakaotalk size={18} />
        </a>
        <a
          href="mailto:taleswiki@proton.me"
          className="hover:text-blue-400"
          aria-label="이메일 보내기"
        >
          <FaEnvelope size={18} />
        </a>
      </div>
    </footer>
  );
}

export default Footer;
