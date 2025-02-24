import { ref, computed, onMounted } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
const backendUrl = import.meta.env.VITE_BACKEND_URL;

const globalToken = ref<string | null>(localStorage.getItem("token"));
const globalId = ref<string | null>(localStorage.getItem("id"));
const globalIsLoggedIn = computed(() => !!globalToken.value);
let isProcessing = false; // 처리 중인지 확인하는 플래그 추가
export function useKakaoAuth() {
  const router = useRouter();

  const handleKakaoLogin = async (): Promise<void> => {
    try {
      const response = await axios.get<{ loginUrl?: string }>(
        backendUrl + "/api/auth/login/kakao/url"
      );
      if (response.data?.loginUrl) {
        window.location.href = response.data.loginUrl;
      } else {
        console.error("카카오 로그인 URL을 가져오지 못했습니다.");
      }
    } catch (error) {
      console.error("카카오 로그인 요청 중 오류 발생:", error);
    }
  };

  const getAuthCode = (): string | null => {
    return new URLSearchParams(window.location.search).get("code");
  };

  const sendKakaoLoginRequest = async (authCode: string): Promise<void> => {
    if (isProcessing) return; // 이미 처리 중이면 리턴

    try {
      isProcessing = true; // 처리 시작

      const response = await axios.post<{ accessToken: string }>(
        backendUrl + "/api/auth/login/kakao?code=" + authCode
      );

      await loginSuccess(response.data.accessToken);

      // URL에서 code 파라미터 제거
      const newUrl = window.location.href.split("?")[0];
      window.history.replaceState({}, document.title, newUrl);

      await router.push("/");
    } catch (error) {
      console.error("로그인 요청 중 오류 발생:", error);
    } finally {
      isProcessing = false; // 처리 완료
    }
  };

  const loginSuccess = async (newToken: string) => {
    localStorage.setItem("token", newToken);
    globalToken.value = newToken;
    if (!localStorage.getItem("id")) {
      try {
        const response = await fetch(backendUrl + "/api/users/me", {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${newToken}`,
          },
        });

        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const data = await response.json();
        globalId.value = data.userId;
        localStorage.setItem("id", data.userId);
        localStorage.setItem("username", data.nickname);
      } catch (error) {
        console.error("Profile loading failed:", error);
      }
    }
  };

  const handleLogout = async (): Promise<void> => {
    try {
      const response = await fetch(backendUrl + "/api/auth/logout", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      localStorage.removeItem("token");
      localStorage.removeItem("id");
      globalToken.value = null;
      globalId.value = null;
    } catch (error) {
      console.error("Profile loading failed:", error);
    }
    await router.push("/"); // 현재 페이지를 홈으로 강제 리다이렉트
    window.location.reload(); // 페이지 새로고침으로 컴포넌트 재평가
  };

  // 컴포넌트가 마운트될 때 한 번만 실행되도록 수정
  onMounted(async () => {
    if (!isProcessing) {
      // 처리 중이 아닐 때만 실행
      globalToken.value = localStorage.getItem("token");
      globalId.value = localStorage.getItem("id");
      const authCode = getAuthCode();
      if (authCode) {
        await sendKakaoLoginRequest(authCode);
      }
    }
  });

  return {
    token: globalToken,
    isLoggedIn: globalIsLoggedIn,
    handleKakaoLogin,
    handleLogout,
    getAuthCode,
    sendKakaoLoginRequest,
  };
}
