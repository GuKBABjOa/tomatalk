package team.overfullow.tolonbgeub.debate.matching.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @EventListener
    public void handleMatchingQueueUpdate(MatchingQueueUpdateEvent event) {
        log.debug("handle matching queue update event: {}", event);
        messagingTemplate.convertAndSend(
                event.destination(),
                event.payload()
        );
    }

    @Async
    @EventListener
    public void handleMatchingSuccess(MatchingSuccessEvent event) {
        log.debug("handle matching success event: {}", event);
        messagingTemplate.convertAndSend(
                event.destination(),
                event.payload()
        );
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String gameId = headerAccessor.getSessionAttributes().get("gameId").toString();
//        String userId = headerAccessor.getUser().getName();
//
//        // todo 매칭에서 플레이어 제거 및 다른 플레이어에게 알림
//        try {
//            GameState state = gameService.handlePlayerDisconnect(gameId, userId);
//            messagingTemplate.convertAndSend("/topic/game/" + gameId,
//                    new GameMessage(MessageType.PLAYER_DISCONNECT,  state));
//        } catch (GameException ex) {
//            log.error("Error handling disconnect for game: {}, user: {}", gameId, userId, ex);
//        }

        // 매칭 취소 알림
        String sessionId = headerAccessor.getSessionId();
        log.info("User Disconnected: {}", sessionId);
    }
}
