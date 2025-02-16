package team.overfullow.tolonbgeub.core.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@RequiredArgsConstructor
public class MessagingExceptionController {

    private final SimpMessagingTemplate messagingTemplate;
// todo 메시징 예외 처리

//    @MessageExceptionHandler
//    public void handleGameException(PlayingException ex, Principal principal) {
//        // 에러 메시지를 클라이언트에게 전송
//        messagingTemplate.convertAndSendToUser(
//                principal.getName(),
//                "/queue/errors",
//                new ErrorMessage()
//        );
//    }
}
