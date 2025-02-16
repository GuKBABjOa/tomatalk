package team.overfullow.tolonbgeub.debate.matching.tmp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import team.overfullow.tolonbgeub.auth.UserId;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.matching.MatchingException;
import team.overfullow.tolonbgeub.debate.playing.PlayingException;
import team.overfullow.tolonbgeub.debate.playing.message.DebateMessage;
import team.overfullow.tolonbgeub.debate.playing.message.DebateMessageType;
import team.overfullow.tolonbgeub.debate.playing.message.payload.request.JoinRequest;

import static java.util.Objects.isNull;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingService matchingService;


    // 서버는 /sub/matching/{category}로 메시지 브로드 캐스팅
    @MessageMapping("/matching.join/{category}")
    public void joinMatching(@DestinationVariable String categoryString,
                           @AuthenticationPrincipal UserId userId,
                           @Payload DebateMessage<JoinRequest> message
    ) {
        log.info("Received matching join request for {}, message = {}", categoryString, message);

        if (isNull(message) || (message.messageType() != DebateMessageType.JOIN)) {
            throw new PlayingException(HttpStatus.BAD_REQUEST, "유효하지 않은 메시지 타입");
        }

        Category category = Category.fromString(categoryString)
                .orElseThrow(() -> new MatchingException(HttpStatus.NOT_FOUND, "존재하지 않는 논제 카테고리입니다."));


        //todo userid 참여자 검증
        matchingService.handleJoin(category, message.payload().userId());
    }

    // todo 매칭 대기열 등록 취소 // stomp or rest?

}
