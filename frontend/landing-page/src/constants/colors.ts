export const colors = {
  // 기본 색상
  primary: {
    main: "#646cff",
    light: "#646cffaa",
    dark: "#535bf2",
  },
  secondary: {
    main: "#61dafb",
    light: "#61dafbaa",
    dark: "#4fa8c7",
  },

  // 텍스트 색상
  text: {
    primary: "#1a1a1a",
    secondary: "#888",
    disabled: "#999",
  },

  // 배경 색상
  background: {
    default: "#ffffff",
    paper: "#f5f5f5",
    dark: "#242424",
  },

  // 상태 색상
  status: {
    success: "#4caf50",
    error: "#f44336",
    warning: "#ff9800",
    info: "#2196f3",
  },

  // 테두리 색상
  border: {
    light: "#e0e0e0",
    main: "#bdbdbd",
    dark: "#9e9e9e",
  },
} as const;

// 타입 정의
export type ColorKey = keyof typeof colors;
export type ColorValue = (typeof colors)[ColorKey];
