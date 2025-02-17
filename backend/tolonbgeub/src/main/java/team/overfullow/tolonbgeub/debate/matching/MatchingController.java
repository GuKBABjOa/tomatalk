package team.overfullow.tolonbgeub.debate.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import team.overfullow.tolonbgeub.auth.UserId;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.matching.message.MatchingMessage;
import team.overfullow.tolonbgeub.debate.matching.message.MatchingMessageType;
import team.overfullow.tolonbgeub.debate.matching.message.request.CancelRequest;
import team.overfullow.tolonbgeub.debate.matching.message.request.JoinRequest;
import team.overfullow.tolonbgeub.debate.playing.PlayingException;
import team.overfullow.tolonbgeub.debate.session.ResilientSessionManager;

import static java.util.Objects.isNull;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingService matchingService;
    private final ApplicationEventPublisher publisher;

    // 서버는 /sub/matching/{category}로 메시지 브로드 캐스팅
    @MessageMapping("/matching.join/{category}")
    public void joinMatching(@DestinationVariable(value = "category") String categoryString,
                             @AuthenticationPrincipal UserId userId,
                             @Payload MatchingMessage<JoinRequest> message
    ) {
        log.debug("Received matching join request for {}, message = {}", categoryString, message);

        if (isNull(message) || (message.messageType() != MatchingMessageType.JOIN)) {
            throw new PlayingException(HttpStatus.BAD_REQUEST, "유효하지 않은 메시지 타입");
        }

        Category category = Category.fromString(categoryString)
                .orElseThrow(() -> new MatchingException(HttpStatus.NOT_FOUND, "존재하지 않는 논제 카테고리입니다."));


        //todo userid 참여자 검증
        matchingService.handleJoin(category, message.payload().userId());
    }

    // todo 매칭 대기열 등록 취소 // stomp or rest?

    @MessageMapping("/matching.cancel/{category}")
    public void cancelMatching(@DestinationVariable(value = "category") String categoryString,
                               @AuthenticationPrincipal UserId userId,
                               @Payload MatchingMessage<CancelRequest> message
    ) {
        log.debug("Received matching cancel request for {}, message = {}", categoryString, message);

        if (isNull(message) || (message.messageType() != MatchingMessageType.CANCEL)) {
            throw new PlayingException(HttpStatus.BAD_REQUEST, "유효하지 않은 메시지 타입");
        }

        Category category = Category.fromString(categoryString)
                .orElseThrow(() -> new MatchingException(HttpStatus.NOT_FOUND, "존재하지 않는 논제 카테고리입니다."));


        //todo userid 참여자 검증
        matchingService.handleCancel(category, message.payload().userId());
    }

}
