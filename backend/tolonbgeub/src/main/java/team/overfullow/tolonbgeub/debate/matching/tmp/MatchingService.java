package team.overfullow.tolonbgeub.debate.matching.tmp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.DebateService;
import team.overfullow.tolonbgeub.debate.matching.MatchingException;
import team.overfullow.tolonbgeub.debate.matching.event.MatchingQueueUpdateEvent;
import team.overfullow.tolonbgeub.debate.matching.event.MatchingSuccessEvent;
import team.overfullow.tolonbgeub.debate.matching.message.MatchingQueueUpdateMessage;
import team.overfullow.tolonbgeub.webrtc.OpenViduHandler;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingQueueManager matchingQueueManager;
    private final ApplicationEventPublisher publisher;
    private final OpenViduHandler openviduHandler;
    private final DebateService debateService;

    /**
     * 매칭 대기열 등록 및 매칭 시도 후 결과 메시지 전송
     *
     * @param category
     * @param userId
     */
    public void handleJoin(Category category, Long userId) {
        MatchingQueue matchingQueue = matchingQueueManager.getByCategory(category); //
        try {
            matchingQueue.register(userId);
            broadcastNewRegister(matchingQueue);

            Optional<MatchingSuccessResult> result = matchingQueue.match();
            result.ifPresent(this::handleMatchingSuccess);
        } catch (IllegalStateException e) {
            throw new MatchingException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error during matching process", e);
            throw new MatchingException(HttpStatus.INTERNAL_SERVER_ERROR, "매칭 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleMatchingSuccess(MatchingSuccessResult result) {
        Long debateId = debateService.create(result.getCategory(), result.getMatchedUserIds());
        sendMatchingSuccessEvent(debateId, result.getMatchedUserIds());
    }

//    /**
//     * 매칭 대기열 등록 취소 요청
//     * @param category
//     * @param userId
//     */
//    public void cancel(Category category, Long userId) {
//        MatchingQueue matchingQueue = matchingQueueManager.getByCategory(category);
//        if (matchingQueue == null) {
//            throw new MatchingException(HttpStatus.NOT_FOUND, "해당 카테고리의 매칭 큐가 존재하지 않습니다.");
//        }
//
//        try {
//            matchingQueue.remove(userId);
//            broadcastNewRegister(matchingQueue);
//        } catch (Exception e) {
//            throw new IllegalStateException("매칭 취소 중 오류가 발생했습니다.", e);
//        }
//    }

    private void broadcastNewRegister(MatchingQueue matchingQueue) {
        var message = MatchingQueueUpdateMessage.builder()
                .category(matchingQueue.getCategory())
                .waitingUserCount(matchingQueue.getWaitingUserCount())
                .build();

        log.info("broadcasting matching queue update: {}", message);
        publisher.publishEvent(MatchingQueueUpdateEvent.builder()
                .category(matchingQueue.getCategory())
                .payload(message)
                .build());
    }

    private void sendMatchingSuccessEvent(Long debateId, List<Long> matchedUserIds) {
        String connectionToken = openviduHandler.createConnectionToken(debateId.toString());
        var message = MatchingSuccessMessage.builder()
                .connectionToken(connectionToken)
                .build();
        matchedUserIds.forEach(userId ->
                publisher.publishEvent(MatchingSuccessEvent.builder()
                        .userId(userId)
                        .payload(message)
                        .build())
        );
    }
}
