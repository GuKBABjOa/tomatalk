package team.overfullow.tolonbgeub.debate.playing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import team.overfullow.tolonbgeub.core.async.PreciseInstantScheduler;
import team.overfullow.tolonbgeub.debate.playing.event.StateUpdateEvent;
import team.overfullow.tolonbgeub.debate.playing.message.PlayingMessage;
import team.overfullow.tolonbgeub.debate.playing.message.PlayingMessageType;
import team.overfullow.tolonbgeub.debate.playing.message.response.PlayingStateResponse;
import team.overfullow.tolonbgeub.debate.playing.state.PlayingStateManager;
import team.overfullow.tolonbgeub.debate.playing.state.PlayingStatus;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayingService {
    private final PlayingStateManager stateManager;
    private final ApplicationEventPublisher eventPublisher;
    private final PreciseInstantScheduler scheduler;

    public void handleJoin(Long debateId, Long userId) {
        log.info("{}: Playing handleJoin", debateId);
        // 토론 참여자들에게 참여자 접속 메시지 전송
        stateManager.join(debateId, userId).ifPresent(stateResponse -> {
            eventPublisher.publishEvent(generateStateUpdateEvent(debateId, stateResponse));
        });

        // 토론 시작: 아래 동기화 구문은 토론 당 단 한 번만 실행되어야 한다
        stateManager.start(debateId).ifPresent(stateResponse -> {
            log.debug("{}: Playing All participants joined! start", debateId);
            handleStateUpdate(debateId, stateResponse);
        });
    }

    public void handleSkip(Long debateId, Long userId) {
        stateManager.skip(debateId, userId).ifPresent(stateResponse -> {
            handleStateUpdate(debateId, stateResponse);
        });
    }

    /**
     * 재귀적으로 이벤트 스케줄링
     */
    private void handleStateUpdate(Long debateId, PlayingStateResponse currentState) {
        log.debug("{}: Playing Broadcast StateUpdate Message, debateId", debateId);
        eventPublisher.publishEvent(generateStateUpdateEvent(debateId, currentState));

        if (currentState.status() == PlayingStatus.FINISHED) {
            return;
        }

        log.debug("{}: Playing 다음 상태 갱신 스케줄링", debateId);
        // 현재 발언자의 발언 시간이 끝날 때, 상태 업데이트 메시징 이벤트 발행
        scheduler.scheduleAtInstant(currentState.currentSpeakEndTime(), () -> {
            // 스케줄된 작업이 유효한지 확인: 최신 상태가 아닌 경우 메시지 발송하지 않는다.
            if(!stateManager.isLatestSequence(debateId, currentState)){
                log.debug("작업이 캔슬되었습니다. currentState.sequence = {} ", currentState.sequence());
                return;
            }
            PlayingStateResponse nextState = stateManager.update(debateId);

            handleStateUpdate(debateId, nextState);
            log.debug("스케줄링된 상태 갱신 수행됨");
        });
    }

    private static StateUpdateEvent generateStateUpdateEvent(Long debateId, PlayingStateResponse playingStateResponse) {
        return StateUpdateEvent.builder()
                .debateId(debateId)
                .payload(PlayingMessage.<PlayingStateResponse>builder()
                        .messageType(PlayingMessageType.STATE_UPDATE)
                        .payload(playingStateResponse)
                        .build())
                .build();
    }

//    public CompletableFuture<Void> handleInterrupt(String debateId, InterruptRequest payload) {
//        if (!canInterrupt(debateId)) {
//            throw new IllegalStateException("Cannot interrupt at this time");
//        }
//
//        String currentSpeaker = debateManager.getCurrentSpeaker(debateId);
//        debateManager.setInterruptState(debateId, true);
//
//        eventPublisher.publishEvent(new InterruptEvent(debateId, payload));
//
//        scheduler.schedule(() -> {
//            debateManager.setInterruptState(debateId, false);
//            debateManager.setCurrentSpeaker(debateId, currentSpeaker);
//            eventPublisher.publishEvent(new InterruptEndEvent(debateId, currentSpeaker));
//        }, interruptDurationSeconds, TimeUnit.SECONDS);
//
//        return CompletableFuture.completedFuture(null);
//    }
//    private boolean canInterrupt(String debateId) {
//        return debateManager.getDebateStatus(debateId) == DebateStatus.IN_PROGRESS &&
//                !debateManager.isInterrupted(debateId);
//    }
}
