package team.overfullow.tolonbgeub.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import team.overfullow.tolonbgeub.game.message.MessageType;
import team.overfullow.tolonbgeub.game.message.GameMessage;
import team.overfullow.tolonbgeub.game.message.payload.GameState;
import team.overfullow.tolonbgeub.game.exception.GameException;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventHandler implements ApplicationListener<SessionDisconnectEvent> {

    // todo SessionDisconnectEvent 처리

    private final GameService gameService;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String gameId = headerAccessor.getSessionAttributes().get("gameId").toString();
        String userId = headerAccessor.getUser().getName();

        // 게임에서 플레이어 제거 및 다른 플레이어에게 알림
        try {
            GameState state = gameService.handlePlayerDisconnect(gameId, userId);
            messagingTemplate.convertAndSend("/topic/game/" + gameId,
                    new GameMessage(MessageType.PLAYER_DISCONNECT,  state));
        } catch (GameException ex) {
            log.error("Error handling disconnect for game: {}, user: {}", gameId, userId, ex);
        }
    }
}
