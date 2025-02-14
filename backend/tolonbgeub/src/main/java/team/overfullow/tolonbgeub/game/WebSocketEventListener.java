package team.overfullow.tolonbgeub.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import team.overfullow.tolonbgeub.matching.MatchingService;
import team.overfullow.tolonbgeub.matching.event.MatchingSuccessEvent;
import team.overfullow.tolonbgeub.matching.event.SubscriberUpdateEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener{

    private final GameService gameService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final MatchingService matchingService;

    // 연결 해제 이벤트
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String gameId = headerAccessor.getSessionAttributes().get("gameId").toString();
//        String userId = headerAccessor.getUser().getName();
//
//        // 게임에서 플레이어 제거 및 다른 플레이어에게 알림
//        try {
//            GameState state = gameService.handlePlayerDisconnect(gameId, userId);
//            messagingTemplate.convertAndSend("/topic/game/" + gameId,
//                    new GameMessage(MessageType.PLAYER_DISCONNECT,  state));
//        } catch (GameException ex) {
//            log.error("Error handling disconnect for game: {}, user: {}", gameId, userId, ex);
//        }

        // 매칭 취소 알림
        String sessionId = headerAccessor.getSessionId();

        matchingService.removeSubscriberFromAllCategories(sessionId);
        log.info("User Disconnected: {}", sessionId);
    }

    @Async
    @EventListener
    public void handleSubscriberUpdate(SubscriberUpdateEvent event) throws InterruptedException {
        log.info("send SubscriberUpdateEvent = {}", event);
        messagingTemplate.convertAndSend(
                event.getDestination(),
                event.getPayload()
        );
    }

    @Async
    @EventListener
    public void handleMatchingSuccess(MatchingSuccessEvent event) throws InterruptedException {
//        log.info("send MatchingSuccessEvent = {}", event);
        messagingTemplate.convertAndSendToUser(
                event.getUserId(),
                event.getDestination(),
                event.getPayload()
        );
        log.info("Message sent to user {}: {}", event.getUserId(), event.getPayload());
    }
}
