package team.overfullow.tolonbgeub.debate.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.DebateService;
import team.overfullow.tolonbgeub.debate.matching.event.MatchingQueueUpdateEvent;
import team.overfullow.tolonbgeub.debate.matching.event.MatchingSuccessEvent;
import team.overfullow.tolonbgeub.debate.matching.message.MatchingMessage;
import team.overfullow.tolonbgeub.debate.matching.message.MatchingMessageType;
import team.overfullow.tolonbgeub.debate.matching.message.response.QueueUpdateResponse;
import team.overfullow.tolonbgeub.debate.matching.message.response.MatchingSuccessResponse;
import team.overfullow.tolonbgeub.debate.matching.queue.MatchingSuccessResult;
import team.overfullow.tolonbgeub.debate.matching.queue.MatchingQueue;
import team.overfullow.tolonbgeub.debate.matching.queue.MatchingQueueManager;
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
            broadcastMatchingQueueUpdate(matchingQueue);

            log.debug("try match");
            matchingQueue.match().ifPresent(result -> {
                handleMatchingSuccess(result);
                broadcastMatchingQueueUpdate(matchingQueue);
            });
        } catch (IllegalStateException e) {
            throw new MatchingException(HttpStatus.BAD_REQUEST, e.getMessage()); //todo 웹소켓 예외 응답 공통 처리
        } catch (Exception e) {
            log.error("Error during matching process", e);
            throw new MatchingException(HttpStatus.INTERNAL_SERVER_ERROR, "매칭 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleMatchingSuccess(MatchingSuccessResult result) {
        log.debug("handleMatchingSuccess result: {}", result);
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

    private void broadcastMatchingQueueUpdate(MatchingQueue matchingQueue) {

        var message = QueueUpdateResponse.builder()
                .category(matchingQueue.getCategory())
                .waitingUserCount(matchingQueue.getWaitingUserCount())
                .build();
        log.debug("broadcasting matching queue update: {}", message);

        matchingQueue.getWaitingUserIds().forEach(id ->
        {

            publisher.publishEvent(MatchingQueueUpdateEvent.builder()
                    .userId(id)
                    .payload(MatchingMessage.<QueueUpdateResponse>builder()
                            .messageType(MatchingMessageType.MATCHING_UPDATE)
                            .payload(message)
                            .build())
                    .build());
        });
    }

    private void sendMatchingSuccessEvent(Long debateId, List<Long> matchedUserIds) {
        String connectionToken = openviduHandler.createConnectionToken(debateId.toString());
        var message = MatchingSuccessResponse.builder()
                .debateId(debateId.toString())
                .connectionToken(connectionToken)
                .build();
        log.debug("sending matching success event: {}", message);
        matchedUserIds.forEach(userId ->
                publisher.publishEvent(MatchingSuccessEvent.builder()
                        .userId(userId)
                        .payload(MatchingMessage.<MatchingSuccessResponse>builder()
                                .messageType(MatchingMessageType.MATCHING_SUCCESS)
                                .payload(message)
                                .build())
                        .build())
        );
    }
}
