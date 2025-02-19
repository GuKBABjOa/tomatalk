export interface Debate {
  id: string;
  title: string;
  category: string;
  duration: number;
  spectatorCount: number;
  participants: number;
  startTime: string;
  endTime: string;
  level?: string; // 선택적 속성으로 추가
}
