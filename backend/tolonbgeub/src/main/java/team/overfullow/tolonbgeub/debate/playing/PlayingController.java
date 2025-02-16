package team.overfullow.tolonbgeub.debate.playing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import team.overfullow.tolonbgeub.auth.UserId;
import team.overfullow.tolonbgeub.debate.playing.message.DebateMessage;
import team.overfullow.tolonbgeub.debate.playing.message.DebateMessageType;
import team.overfullow.tolonbgeub.debate.playing.message.payload.request.JoinRequest;

import static java.util.Objects.isNull;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PlayingController {
    private final PlayingService playingService;


    @MessageMapping("/debate.join/{debateId}")
    public void joinDebate(@DestinationVariable Long debateId,
                           @AuthenticationPrincipal UserId userId,
                           @Payload DebateMessage<JoinRequest> message
    ) {
        log.info("Received join request for {}, message = {}", debateId, message);

        if (isNull(message) || (message.messageType() != DebateMessageType.JOIN)) {
            throw new PlayingException(HttpStatus.BAD_REQUEST, "유효하지 않은 메시지 타입");
        }

        //todo userid 참여자 검증
        playingService.handleJoin(debateId, message.payload());
    }

//    @MessageMapping("/debate.interrupt/{debateId}")
//    public void interrupt(@DestinationVariable String debateId,
//                          @Payload DebateMessage<Interrupt> message,
//                          @Header("simpSessionId") String sessionId) {
//        playingService.handleInterrupt(debateId, message.payload());
//                });
//    }
}
