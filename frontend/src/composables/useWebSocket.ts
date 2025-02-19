import { ref, onMounted, onUnmounted } from "vue";
import { Client } from '@stomp/stompjs'
import { useMatchingStore } from "@/stores/matchingStore";


export function useWebSocket(url: string, reconnectInterval = 5000) { // 🔥 5초마다 재연결 시도
  const socket = ref<WebSocket | null>(null);
  const messages = ref<any[]>([]);
  const isConnected = ref(false);
  const token = ref(localStorage.getItem("token"));
  const matchingStore = useMatchingStore()
  const debateId = matchingStore.matchingDetails.debateId
  // const stompClient = new Client({
  //   brokerURL: url,
  //   connectHeaders: { Authorization: `Bearer ${token.value}` },
  //   debug: (str) => console.log(str),
  //   reconnectDelay: 5000,
  //   onConnect: () => {
  //     console.log("WebSocket Connected");
  //     const userId = localStorage.getItem("id");
      
  //     // First subscription - for general matching updates
  //     stompClient.subscribe(
  //       `/debate/${debateId}`,
  //       async (MESSAGE) => {
  //         const data = JSON.parse(MESSAGE.body);
  //         console.log('Received Message from matching topic:', data);
  //         console.log(data.messageType)
  //       })
  //     stompClient.publish({
  //         destination: `/debate.join/${debateId}`,
  //         headers: {
  //           Authorization: `Bearer ${token.value}`,
  //           "Content-Type": "application/json",
  //         },
  //         body: JSON.stringify({
  //           "messageType": "JOIN",
  //           "payload": {"userId": userId},
  //         }),
  //       });
  //     },
  //     onDisconnect: () => console.log("WebSocket Disconnected"),
  //     })

  const debateState = ref<any>(null); // 토론 상태
  let reconnectTimeout: NodeJS.Timeout | null = null; // 🔥 재연결 타이머
  const stompClient = ref<Client | null>(null);
  const connect = () => {
    console.log("🔄 Connecting to WebSocket via STOMP:", url);
    
    // Initialize StompClient
    stompClient.value = new Client({
      brokerURL: url,
      connectHeaders: { Authorization: `Bearer ${token.value}` },
      debug: (str) => console.log(str),
      reconnectDelay: reconnectInterval,
      
      onConnect: () => {
        console.log("✅ STOMP WebSocket Connected");
        isConnected.value = true;
        
        const userId = localStorage.getItem("id");
        
        // Subscribe to debate topic
        stompClient.value?.subscribe(
          `/sub/debate/${debateId}`,
          async (message) => {
            const data = JSON.parse(message.body);
            console.log('Received Message from debate topic:', data);
            
            // Update debate state
            debateState.value = data.payload;
            
            // Add to messages history
            messages.value.push(data);
            
            console.log(data.messageType);
          }
        );
        
        // Send JOIN message
        stompClient.value?.publish({
          destination: `/pub/debate.join/${debateId}`,
          headers: {
            Authorization: `Bearer ${token.value}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            "messageType": "JOIN",
            "payload": {"userId": userId},
          }),
        });
      },
      
      onDisconnect: () => {
        console.log("❌ STOMP WebSocket Disconnected");
        isConnected.value = false;
      },
      
      onStompError: (error) => {
        console.error("⚠️ STOMP Error:", error);
        isConnected.value = false;
      }
    });
    
    // Activate the connection
    stompClient.value.activate();
  };
  // const connect = () => {
  //   console.log("🔄 WebSocket 연결 시도:", url);
  //   socket.value = new WebSocket(url);

  //   socket.value.onopen = () => {
  //     isConnected.value = true;
  //     console.log("✅ WebSocket 연결됨:", url);

  //     // 🔥 재연결 성공 시 기존 타이머 제거
  //     if (reconnectTimeout) {
  //       clearTimeout(reconnectTimeout);
  //       reconnectTimeout = null;
  //     }
  //   };

  //   socket.value.onmessage = (event) => {
  //     const data = JSON.parse(event.data);
  //     console.log("📩 받은 메시지:", data);

  //     // 항상 debateState 업데이트 (type과 관계없이)
  //     debateState.value = data.payload;

  //     messages.value.push(data);
  //   };

  //   socket.value.onclose = () => {
  //     isConnected.value = false;
  //     console.log("❌ WebSocket 연결 종료됨. 재연결 시도...");

  //     // 웹소켓이 끊어졌을 때 자동 재연결
  //     attemptReconnect();
  //   };

  //   socket.value.onerror = (error) => {
  //     console.error("⚠️ WebSocket 오류 발생:", error);
  //     socket.value?.close(); // 오류 발생 시 강제로 소켓 닫기
  //   };
  // };

  // const attemptReconnect = () => {
  //   if (!isConnected.value && !reconnectTimeout) {
  //     reconnectTimeout = setTimeout(() => {
  //       console.log("🔄 WebSocket 재연결 중...");
  //       connect();
  //     }, reconnectInterval); // 지정된 시간 후 재연결 (기본값: 5초)
  //   }
  // };

  onMounted(connect);
  onUnmounted(() => {
    if (stompClient.value && stompClient.value.active) {
      console.log("Deactivating STOMP client...");
      stompClient.value.deactivate();
    }
  });
  // onUnmounted(() => {
  //   if (socket.value) {
  //     socket.value.close();
  //   }
  //   if (reconnectTimeout) {
  //     clearTimeout(reconnectTimeout);
  //   }
  // });

  return { socket, messages, isConnected, debateState };
}
