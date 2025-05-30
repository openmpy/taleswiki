const adjectives = [
  "달리는",
  "웃고있는",
  "멍때리는",
  "화난",
  "졸린",
  "신난",
  "춤추는",
  "슬퍼하는",
  "자고있는",
  "노래하는",
  "누워있는",
];

const characters = [
  "초원",
  "밍밍",
  "리나",
  "빅보",
  "DnD",
  "나르시스",
  "마키",
  "러프",
  "히든러프",
  "바다",
  "카이",
  "유키",
  "쿠로",
  "아벨",
  "하루",
  "베라",
  "손오공",
  "시호",
  "루시",
  "미호",
  "R",
  "하랑",
  "라라",
  "연오",
  "블러디 베라",
  "시오넬",
  "담연",
  "클로에",
  "시드",
  "티티",
  "엘림스 스마일",
  "카인",
  "로로아",
  "셀리아",
];

export const generateRandomNickname = () => {
  const randomAdjective =
    adjectives[Math.floor(Math.random() * adjectives.length)];
  const randomCharacter =
    characters[Math.floor(Math.random() * characters.length)];
  return `${randomAdjective} ${randomCharacter}`;
};

export const getNickname = () => {
  const storedNickname = localStorage.getItem("chat-nickname");
  if (storedNickname) {
    return storedNickname;
  }
  const newNickname = generateRandomNickname();
  localStorage.setItem("chat-nickname", newNickname);
  return newNickname;
};
