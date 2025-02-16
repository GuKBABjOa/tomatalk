package team.overfullow.tolonbgeub.debate.playing;

import com.google.errorprone.annotations.ThreadSafe;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Getter
@ThreadSafe
public class PlayingState {
    private static final int waitingTimeSeconds = 10;
    private static final int speakTimeSeconds = 15; // todo 테스트를 위해 짧게 설정

    private AtomicInteger sequence = new AtomicInteger(0);
    /*
    6: 내 발언 10분에 끝
    7: 끼어들기 6분 15초에 끝 (6번 상태 기록)
    8: 내 발언 10분에 끝 7에서 기록한 6번 상태 메시지 다시 발행
     */

    private final Object lock = new Object();
    private final Object participantsLock = new Object();
    private final int maxTurnCount = 8;
    private final Long debateId;

    private int turnCounter = 0;
    private PlayingStatus status; // 현재 토론 상태
    private Long currentSpeakerId; // 현재 발언자
    private Instant currentSpeakEndTime; // 현재 발언자의 발언 종료 시각
    private Long nextSpeakerId; // 다음 발언자
    private boolean interrupted; // 끼어들기(POI) 중인지?
    private Long interruptSpeakerId; // 끼어들기(POI) 발언자
    private Instant interruptEndTime;//끼어들기(POI) 발언 종료 시각
    private Set<PlayingUser> participants = new CopyOnWriteArraySet<>(); // 토론 참여자 리스트

    public static PlayingState init(Long debateId, List<PlayingUser> participants) {
        PlayingState playingState = PlayingState.builder()
                .debateId(debateId)
                .participants(participants)
                .build();
        playingState.setDefaultState(PlayingStatus.WAITING);
        return playingState;
    }

    @Builder
    private PlayingState(Long debateId,
                         PlayingStatus status,
                         Long currentSpeakerId,
                         Instant currentSpeakEndTime,
                         Long nextSpeakerId,
                         boolean interrupted,
                         Long interruptSpeakerId,
                         Instant interruptEndTime,
                         Collection<PlayingUser> participants
    ) {
        this.debateId = debateId;
        this.status = status;
        this.currentSpeakerId = currentSpeakerId;
        this.currentSpeakEndTime = currentSpeakEndTime;
        this.nextSpeakerId = nextSpeakerId;
        this.interrupted = interrupted;
        this.interruptSpeakerId = interruptSpeakerId;
        this.interruptEndTime = interruptEndTime;
        this.participants.addAll(participants);
    }

    // todo check sequence

    public boolean canInterrupt() { // 끼어들기 가능한지?
        return !interrupted; // todo 보호 시간 고려
    }

    public void start() {
        this.status = PlayingStatus.STARTED;
        this.nextSpeakerId = findFirstSpeaker().getUserId();
        this.currentSpeakEndTime = Instant.now().plusSeconds(waitingTimeSeconds);
    }

    public void participate(Long userId) {
        log.info("participate: userId = {}", userId);
        synchronized (participantsLock) {
            participants.stream()
                    .filter(player -> player.getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(u -> u.setParticipant(true));
        }
    }

    public boolean canStart(int expectedCount) {
        synchronized (participantsLock) {
            long count = participants.stream().filter(PlayingUser::isParticipant).count();
            System.out.println("count = " + count);
            return !isStarted() && count == expectedCount;
        }
    }

    private boolean isStarted() {
        return status != PlayingStatus.WAITING;
    }

    // todo 업데이트 순서에 맞는 지 검증
    public void update() {
        if (++turnCounter > maxTurnCount) {
            setDefaultState(PlayingStatus.FINISHED);
            log.info("Debate {}: finished", debateId);
            return;
        }
        log.info("Debate {}: turn {} update state", debateId, turnCounter);
        try{
            Long currentSpeakerId = this.nextSpeakerId;
            Long nextSpeakerId = findNextSpeakerId(currentSpeakerId);
            setDefaultState(PlayingStatus.IN_PROGRESS);
            this.currentSpeakerId = currentSpeakerId;
            this.nextSpeakerId = nextSpeakerId;
            this.currentSpeakEndTime = Instant.now().plusSeconds(speakTimeSeconds);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("업데이트 예외 발생: {}", e.getMessage());
        }
    }

    private Long findNextSpeakerId(Long currentSpeakerId) {
        Optional<PlayingUser> currentSpeakerOptional = participants.stream()
                .filter(player -> Objects.equals(player.getUserId(), currentSpeakerId))
                .findFirst();
        if (currentSpeakerOptional.isEmpty()) {
            return findFirstSpeaker().getUserId();
        }

        PlayingUser currentSpeaker = currentSpeakerOptional.get();
        int order = currentSpeaker.getSpeechOrder();
        int nextOrder = order + 1;
        return participants.stream()
                .filter(player -> player.getSpeechOrder() == nextOrder)
                .findAny()
                .orElse(findFirstSpeaker())
                .getUserId();
    }

    private PlayingUser findFirstSpeaker() {
        return participants.stream()
                .filter(player -> player.getSpeechOrder() == 1)
                .findFirst()
                .orElseThrow(()->new IllegalStateException("토론의 첫 발언자를 찾을 수 없습니다"));
    }

    private void setDefaultState(PlayingStatus status) {
        this.status = status;
        this.currentSpeakerId = null;
        this.currentSpeakEndTime = null;
        this.nextSpeakerId = null;
        this.interrupted = false;
        this.interruptSpeakerId = null;
        this.interruptEndTime = null;
    }
}
