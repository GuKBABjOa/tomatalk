package team.overfullow.tolonbgeub.debate.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import team.overfullow.tolonbgeub.webrtc.OpenViduConfigProps;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, Queue<WebSocketSession>> topicQueues = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final OpenViduConfigProps openviduConfigProps;
    private final String HEADER_TOPIC_ID = "X-Topic-Id";
    private final String HEADER_USER_ID = "X-User-Id";

    /*
    매칭 대기열 등록
    1. C->S: topicId에 해당하는 큐에 대기 요청
    2. S: 각 topicId 별로 2인 이상 대기 중일 경우 매칭 시도

    매칭 시도 로직
    1. S: 토픽 큐에서 가장 앞에서 2명 꺼냄
    2. 꺼내기 전 여전히 연결 중인지 확인
    3. 연결이 끊겼다면 버림
    3-1. 큐에 남은 세션이 존재하면 2 반복
    3-2. 큐에 남은 세션이 없으면 꺼냈던 세션 다시 큐에 넣고 종료
    4. 큐에서 두 개의 세션 꺼내는 데 성공하면 매칭 성공

    매칭 성공 시
    1. S: 게임 메타데이터 생성(gameId, 참여 userIds, 생성 시간, etc...)
    2. S: DB에 저장
    3. S->C: 매칭 성공 메시지 전달 with 게임 메타데이터
    4. C->S: 매칭 성공 메시지 수신 후 close connection

    클라이언트가 매칭 취소 시
    1.
     */

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // todo header... or body? Game이랑 Matching 통일하는 게 좋을 거 같음
        // todo Authorization header에서 토큰 확인 후 일치하지 않으면 연결 강제 종료
        String topicId = getFromHeaders(session, HEADER_TOPIC_ID);
        topicQueues.putIfAbsent(topicId, new ConcurrentLinkedQueue<>());
        topicQueues.get(topicId).offer(session);
        checkAndMatch(topicId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String topic = getFromHeaders(session, HEADER_TOPIC_ID);
        topicQueues.getOrDefault(topic, new ConcurrentLinkedQueue<>()).remove(session);
    }

//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//        // JSON 메시지를 DTO로 변환
//        MatchingRequestDto requestDto = objectMapper.readValue(message.getPayload(), MatchingRequestDto.class);
//    }

    private void checkAndMatch(String topic) {
        Queue<WebSocketSession> queue = topicQueues.get(topic);

        // todo refactor: checkcondition -> 예외 처리 사용해서 분기 제거
        if (isNull(queue) || queue.size() < 2) {
            return; // 2명 이상 대기 중일 때만 매칭 진행
        }

        matchPlayers(queue);
    }

    private void matchPlayers(Queue<WebSocketSession> queue) {
        WebSocketSession player1 = null;
        WebSocketSession player2 = null;

        // 큐에서 소켓이 연결된 두 클라이언트 찾기
        while (!queue.isEmpty() && (player1 == null || player2 == null)) {
            WebSocketSession session = queue.poll();
            if (!isNull(session) && session.isOpen()) {
                if (isNull(player1)) {
                    player1 = session;
                } else {
                    player2 = session;
                }
            }
        }

        if (!isNull(player1) && !isNull(player2)) {
            afterMatchingSuccess(player1, player2);
        } else {
            putIfExists(player1, queue);
            putIfExists(player2, queue);
        }
    }

    private void putIfExists(WebSocketSession player1, Queue<WebSocketSession> queue) {
        if (!isNull(player1) && player1.isOpen()) queue.offer(player1);
    }

    private void afterMatchingSuccess(WebSocketSession player1, WebSocketSession player2) {
        try {

            // 게임 메타데이터 생성 및 DB에 저장

            MatchingSuccessDto responseDto = MatchingSuccessDto.builder()
                    .serverUrl(openviduConfigProps.getUrl()) // todo game 쪽으로 넘겨야 할 거 같음
                    .gameId("CreatedGameId")
                    .userIds(List.of( // DB에 저장, 응답으로는 넘기지 않음
                            getFromHeaders(player1, HEADER_USER_ID),
                            getFromHeaders(player2, HEADER_USER_ID)))
                    .build();

            String responseJson = objectMapper.writeValueAsString(responseDto);

            player1.sendMessage(new TextMessage(responseJson));
            player2.sendMessage(new TextMessage(responseJson));

            player1.close();
            player2.close();
        } catch (IOException e) {
            log.error("afterMatchingSuccess:", e);
        }
    }

    private String getFromHeaders(WebSocketSession session, String headerName) {
        return session.getHandshakeHeaders().getFirst(headerName);
    }
}

