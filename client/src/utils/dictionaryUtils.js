// 한글 초성 배열 (쌍자음 제외)
export const koreanInitials = [
  "ㄱ",
  "ㄴ",
  "ㄷ",
  "ㄹ",
  "ㅁ",
  "ㅂ",
  "ㅅ",
  "ㅇ",
  "ㅈ",
  "ㅊ",
  "ㅋ",
  "ㅌ",
  "ㅍ",
  "ㅎ",
];

// 영문 알파벳 배열
export const englishInitials = Array.from("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

// 숫자 배열
export const numberInitials = Array.from("0123456789");

// 초기 그룹 구조 생성
export const createInitialGroups = () => {
  const groups = [
    ...koreanInitials.map((initial) => ({ initial, dictionaries: [] })),
    ...englishInitials.map((initial) => ({ initial, dictionaries: [] })),
    ...numberInitials.map((initial) => ({ initial, dictionaries: [] })),
  ];
  return groups;
};

export const getStatusInfo = (status) => {
  switch (status) {
    case "ALL_ACTIVE":
      return { text: "활성", className: "bg-green-100 text-green-800" };
    case "READ_ONLY":
      return { text: "읽기", className: "bg-orange-100 text-orange-800" };
    case "HIDDEN":
      return { text: "숨김", className: "bg-gray-100 text-gray-800" };
    default:
      return { text: "오류", className: "bg-red-100 text-red-800" };
  }
};

// 한글 초성 추출 함수
export const getKoreanInitial = (text) => {
  const firstChar = text.charAt(0);
  const code = firstChar.charCodeAt(0);

  if (code >= 0xac00 && code <= 0xd7a3) {
    const initialCode = Math.floor((code - 0xac00) / 28 / 21);
    // 쌍자음을 기본 자음으로 매핑
    const initialMap = {
      0: "ㄱ",
      1: "ㄱ", // ㄱ, ㄲ -> ㄱ
      2: "ㄴ",
      3: "ㄷ",
      4: "ㄷ", // ㄷ, ㄸ -> ㄷ
      5: "ㄹ",
      6: "ㅁ",
      7: "ㅂ",
      8: "ㅂ", // ㅂ, ㅃ -> ㅂ
      9: "ㅅ",
      10: "ㅅ", // ㅅ, ㅆ -> ㅅ
      11: "ㅇ",
      12: "ㅈ",
      13: "ㅈ", // ㅈ, ㅉ -> ㅈ
      14: "ㅊ",
      15: "ㅋ",
      16: "ㅌ",
      17: "ㅍ",
      18: "ㅎ",
    };
    return initialMap[initialCode];
  }
  return null;
};

// 초성 분류 함수
export const classifyByInitial = (title) => {
  const firstChar = title.charAt(0).toUpperCase();

  // 한글인 경우
  const koreanInitial = getKoreanInitial(title);
  if (koreanInitial) return koreanInitial;

  // 영문인 경우
  if (/[A-Z]/.test(firstChar)) return firstChar;

  // 숫자인 경우
  if (/[0-9]/.test(firstChar)) return firstChar;

  // 기타 문자인 경우 '#'으로 분류
  return "#";
};
