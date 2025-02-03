package team.overfullow.tolonbgeub.game.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler
    public void handleGameException(GameException ex, Principal principal) {
        // 에러 메시지를 클라이언트에게 전송
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                new ErrorMessage(ex.getErrorCode(), ex.getMessage())
        );
    }
}
