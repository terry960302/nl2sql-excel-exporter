import { colors } from "../constants/colors";

/**
 * 색상 값을 가져오는 함수
 * @param path - 색상 경로 (예: 'primary.main', 'text.primary')
 * @returns 색상 값
 */
export const getColor = (path: string): string => {
  const keys = path.split(".");
  let current: any = colors;

  for (const key of keys) {
    if (current[key] === undefined) {
      console.warn(`Color not found: ${path}`);
      return "#000000";
    }
    current = current[key];
  }

  return current;
};

/**
 * 색상의 투명도 조절
 * @param color - HEX 색상 코드
 * @param opacity - 투명도 (0-1)
 * @returns rgba 색상 값
 */
export const setOpacity = (color: string, opacity: number): string => {
  // HEX를 RGB로 변환
  const r = parseInt(color.slice(1, 3), 16);
  const g = parseInt(color.slice(3, 5), 16);
  const b = parseInt(color.slice(5, 7), 16);

  return `rgba(${r}, ${g}, ${b}, ${opacity})`;
};
