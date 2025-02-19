// composables/useTimer.ts
import { ref, computed, watch, onUnmounted } from "vue";

export function useTimer(currentSpeakEndTime: any) {
  const remainingTime = ref<number>(0); // 남은 초
  const totalDuration = ref<number>(0); // 전체 발언 시간
  const timerInterval = ref<NodeJS.Timeout | null>(null);

  // 타이머 진행률 (0~100%)
  const timerProgress = computed(() => {
    return totalDuration.value > 0 ? (remainingTime.value / totalDuration.value) * 100 : 0;
  });

  // 타이머 텍스트 (MM:SS 형식)
  const formattedTime = computed(() => {
    const minutes = Math.floor(remainingTime.value / 60);
    const seconds = remainingTime.value % 60;
    return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
  });

  // 타이머 시작 함수
  const startTimer = () => {
    if (!currentSpeakEndTime.value) return;

    // 발언 종료 시간을 Date 객체로 변환
    const endTime = new Date(currentSpeakEndTime.value).getTime();
    const now = new Date().getTime();
    
    // 전체 발언 시간 계산
    totalDuration.value = Math.floor((endTime - now) / 1000);
    remainingTime.value = totalDuration.value;

    // 기존 타이머 정리
    if (timerInterval.value) clearInterval(timerInterval.value);

    // 1초마다 타이머 업데이트
    timerInterval.value = setInterval(() => {
      const now = new Date().getTime();
      remainingTime.value = Math.max(0, Math.floor((endTime - now) / 1000));

      // 시간이 0이 되면 타이머 종료
      if (remainingTime.value <= 0) {
        clearInterval(timerInterval.value!);
        timerInterval.value = null;
      }
    }, 1000);
  };

  // `currentSpeakEndTime`이 변경될 때 타이머 재시작
  watch(currentSpeakEndTime, (newTime) => {
    if (newTime) startTimer();
  });

  // 컴포넌트가 언마운트될 때 타이머 정리
  onUnmounted(() => {
    if (timerInterval.value) clearInterval(timerInterval.value);
  });

  return {
    remainingTime,
    totalDuration,
    timerProgress,
    formattedTime,
  };
}
