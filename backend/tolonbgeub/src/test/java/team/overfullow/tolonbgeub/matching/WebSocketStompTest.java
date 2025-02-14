package team.overfullow.tolonbgeub.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.matching.message.SubscriberUpdateMessage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WebSocketStompTest {

    private static final String WEBSOCKET_ENDPOINT = "ws://localhost:%d/ws";

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;
    private WebSocketStompClient stompClient;
    private WebSocketHttpHeaders headers;
    private StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            log.info("WebSocket 연결 성공!");
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            log.error("WebSocket 연결 오류: " + exception.getMessage());
        }
    };

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        headers = new WebSocketHttpHeaders();
    }

    @Test
    void testWebSocketConnectionAndMessaging() throws Exception {
        BlockingQueue<SubscriberUpdateMessage> messages = new LinkedBlockingDeque<>();

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("X-User-Id", "userId");
        stompHeaders.setDestination("/sub/matching.POLITICS"); // 목적지 설정
        StompSession session = stompClient.connectAsync(
                String.format(WEBSOCKET_ENDPOINT, port),
                headers,
                stompHeaders,
                sessionHandler
        ).get(3, TimeUnit.SECONDS); // 타임아웃 증가

        CountDownLatch latch = new CountDownLatch(1);
        session.subscribe(stompHeaders, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    SubscriberUpdateMessage message =
                            objectMapper.readValue((byte[]) payload, SubscriberUpdateMessage.class); // JSON 파싱
                    log.info("📩 메시지 변환 완료: {}", message);
                    latch.countDown();
                    messages.add(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        });


        SubscriberUpdateMessage take = messages.poll(1, TimeUnit.SECONDS);
        SoftAssertions.assertSoftly(softly -> {
            assertNotNull(take, "메시지를 받지 못했습니다.");
            assertEquals(Category.POLITICS, take.getCategory());
            assertEquals(1, take.getSubscriberCount());
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS), "정해진 메시지 개수만큼 수신하지 못했습니다.");
    }

    @Test
    void testConcurrentWebSocketConnections() throws Exception {
        int clientCount = 16; // 동시에 연결할 클라이언트 수
        BlockingQueue<SubscriberUpdateMessage> messages = new LinkedBlockingDeque<>();
        CountDownLatch latch = new CountDownLatch(clientCount/4*4);
        CountDownLatch latch2 = new CountDownLatch(clientCount/4*4);
        int timeout = 1+clientCount/10; // 타임아웃 시간을 3초에서 10초로 증가
        // ExecutorService를 사용하여 여러 클라이언트 동시에 실행
        ExecutorService executorService = Executors.newFixedThreadPool(clientCount);

        for (int i = 0; i < clientCount; i++) {
            int clientId = i + 1; // 클라이언트 ID는 1부터 시작
            String userId = "userId" + clientId;

            StompHeaders userStompHeaders = new StompHeaders();
            userStompHeaders.set("X-User-Id", userId);
            userStompHeaders.setSession(userId);
            userStompHeaders.setDestination("/user/" + userId + "/matching.POLITICS"); // 목적지 설정

            executorService.submit(() -> {
                try {
                    // 클라이언트 연결 및 세션 처리
                    StompSession session = stompClient.connectAsync(
                            String.format(WEBSOCKET_ENDPOINT, port),
                            headers, sessionHandler
                    ).get(timeout, TimeUnit.SECONDS); // 타임아웃 시간 증가

                    // 각 클라이언트가 구독할 엔드포인트를 지정
                    session.subscribe(userStompHeaders, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return Object.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            try {
                                SubscriberUpdateMessage message =
                                        objectMapper.readValue((byte[]) payload, SubscriberUpdateMessage.class); // JSON 파싱
                                log.info("📩 개인 클라이언트 {}: 메시지 변환 완료: {}", clientId, message);
//                                messages.add(message);
                                latch2.countDown(); // 메시지를 받으면 카운트다운
                                latch2.await();
                            } catch (IOException e) {
                                log.error("개인 클라이언트 {}에서 메시지 처리 중 오류 발생: {}", clientId, e.getMessage());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } catch (Exception e) {
                    log.error("개인 클라이언트 {}에서 WebSocket 연결 중 오류 발생: {}", clientId, e.getMessage());
                }
            });

            StompHeaders stompHeaders = new StompHeaders();
            stompHeaders.set("X-User-Id", userId);
            stompHeaders.setSession(userId);
            stompHeaders.setDestination("/sub/matching.POLITICS"); // 목적지 설정

            executorService.submit(() -> {
                try {
                    // 서버 연결 및 세션 처리
                    StompSession session = stompClient.connectAsync(
                            String.format(WEBSOCKET_ENDPOINT, port),
                            headers, sessionHandler
                    ).get(timeout, TimeUnit.SECONDS); // 타임아웃 시간 증가

                    // 각 클라이언트가 구독할 엔드포인트를 지정
                    session.subscribe(stompHeaders, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return Object.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            try {
                                SubscriberUpdateMessage message =
                                        objectMapper.readValue((byte[]) payload, SubscriberUpdateMessage.class); // JSON 파싱
//                                log.info("📩 클라이언트 {}: 메시지 변환 완료: {}", clientId, message);
//                                messages.add(message);
                                latch.countDown(); // 메시지를 받으면 카운트다운
                                latch.await();
                            } catch (IOException e) {
                                log.error("클라이언트 {}에서 메시지 처리 중 오류 발생: {}", clientId, e.getMessage());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                } catch (Exception e) {
                    log.error("클라이언트 {}에서 WebSocket 연결 중 오류 발생: {}", clientId, e.getMessage());
                }
            });
        }

        // 모든 클라이언트가 메시지를 수신했는지 확인
//        boolean allMessagesReceived = latch.await(timeout, TimeUnit.SECONDS);
//        assertTrue(allMessagesReceived, "모든 클라이언트가 메시지를 수신하지 못했습니다.");
        boolean allMessagesReceived2 = latch2.await(timeout, TimeUnit.SECONDS);
        assertTrue(allMessagesReceived2, "모든 클라이언트가 메시지를 수신하지 못했습니다.");

        // 각 클라이언트가 수신한 메시지에 대해 검증
//        SoftAssertions.assertSoftly(softly -> {
//            messages.forEach(message -> {
//                assertNotNull(message, "메시지가 null입니다.");
//                assertEquals(Category.POLITICS, message.getCategory(), "카테고리가 일치하지 않습니다.");
//                assertThat(message.getSubscriberCount()).isLessThanOrEqualTo(clientCount/4*4);
//            });
//        });

        executorService.shutdown(); // Executor 종료
    }


}
