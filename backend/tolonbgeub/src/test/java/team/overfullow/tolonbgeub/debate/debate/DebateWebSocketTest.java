package team.overfullow.tolonbgeub.debate.debate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import team.overfullow.tolonbgeub.ApiTestSupport;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.service.DebateService;
import team.overfullow.tolonbgeub.debate.playing.message.PlayingMessage;
import team.overfullow.tolonbgeub.debate.playing.message.PlayingMessageType;
import team.overfullow.tolonbgeub.debate.playing.message.request.JoinRequest;
import team.overfullow.tolonbgeub.debate.playing.message.response.PlayingStateResponse;
import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.dto.UserResponse;
import team.overfullow.tolonbgeub.user.service.UserService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DebateWebSocketTest extends ApiTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private DebateService debateService;

    @LocalServerPort
    private int port;


    private WebSocketStompClient stompClient;
    private WebSocketHttpHeaders headers;
    private List<StompSession> sessions = new ArrayList<>();
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
    void setUp() {
        // STOMP Client 설정
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        headers = new WebSocketHttpHeaders();
    }

    @AfterEach
    void tearDown() {
        sessions.forEach(StompSession::disconnect);
    }

    @Test
    @DisplayName("유저가 토론에 참여할 때마다 Join Message를 전달 받고, 4명의 유저가 모이면 Start Message를 전달받는다.")
    void shouldJoinDebateAndReceiveMessages() throws Exception {
        // given
        CountDownLatch latch = new CountDownLatch(4);
        BlockingQueue<PlayingMessage<PlayingStateResponse>> messages = new LinkedBlockingQueue<>();

        // 1️⃣ 4명의 사용자 생성
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String username = "debateTestUser" + i;
            UserResponse user = userService.createUser(UserRequest.builder()
                    .email(username + "@test.email")
                    .nickname(username)
                    .build());
            userIds.add(user.userId());
        }

        // 2️⃣ Debate 생성
        Category category = Category.POLITICS; // 적절한 카테고리 선택
        Long debateId = debateService.create(category, userIds);

        log.info("@@@@@@@@@@ test debateId: " + debateId);

        // 3️⃣ WebSocket 연결 및 구독
        String wsUrl = "ws://localhost:" + port + "/ws";

        // when
        for (Long userId : userIds) {
            StompHeaders stompHeaders = new StompHeaders();
            stompHeaders.set("X-User-Id", "userId");
            stompHeaders.setDestination("/sub/debate/"+debateId); // 목적지 설정
            StompSession session = stompClient.connectAsync(
                    wsUrl,
                    headers,
                    stompHeaders,
                    sessionHandler
            ).get(3, TimeUnit.SECONDS); // 타임아웃 증가
            sessions.add(session);

            session.subscribe("/sub/debate/" + debateId, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return PlayingMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    PlayingMessage<PlayingStateResponse> stateUpdateMessage = (PlayingMessage<PlayingStateResponse>) payload;
                    messages.add(stateUpdateMessage);
                    System.out.println("stateUpdateMessage = " + stateUpdateMessage);
                    latch.countDown();
                }
            });

            // 4️⃣ 토론 참여 메시지 전송
            PlayingMessage<JoinRequest> joinMessage = PlayingMessage.<JoinRequest>builder()
                    .messageType(PlayingMessageType.JOIN)
                    .payload(new JoinRequest(userId))
                    .build();
            session.send("/pub/debate.join/" + debateId, joinMessage);
            Thread.sleep(100); // 순차적으로 입장했음을 가정하기 위해 100ms 딜레이
        }
        // then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register the module to handle java.time types
        // 5️⃣ 메시지 수신 및 검증
        var out = messages.stream()
                .map(msg -> {
                    PlayingStateResponse playingStateResponse = objectMapper.convertValue(msg.payload(), PlayingStateResponse.class);
                    return playingStateResponse.status();
                })
//                .filter(s -> s == DebateStatus.WAITING)
                .toList();
        assertTrue(latch.await(14, TimeUnit.SECONDS), "모든 사용자로부터 메시지를 수신해야 함");
        assertThat(out).hasSize(14); //1+2+3+4 + 4
    }
}
