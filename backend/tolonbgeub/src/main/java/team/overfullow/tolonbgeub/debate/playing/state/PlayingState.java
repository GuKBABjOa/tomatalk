package team.overfullow.tolonbgeub.debate.playing.state;

import com.google.errorprone.annotations.ThreadSafe;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.debate.playing.PlayingException;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Getter
@ThreadSafe
public class PlayingState {
    private static final int waitingTimeSeconds = 5;
    private static final int speechingTimeSeconds = 10; // todo 테스트를 위해 짧게 설정
    private static final int preparingTimeSeconds = 15; // todo 테스트를 위해 짧게 설정


    private final ReentrantLock stateLock = new ReentrantLock();
    private final int maxTurnCount = 8;
    private final Long debateId;

    /*
    sequence: 상태 변경 순서를 보장
    6: 내 발언 10분에 끝
    7: 끼어들기 6분 15초에 끝 (6번 상태 기록)
    8: 내 발언 10분에 끝 7에서 기록한 6번 상태 메시지 다시 발행
     */
    private final AtomicInteger sequence = new AtomicInteger(0);

    private int turnCounter = 0;
    private PlayingStatus status; // 현재 토론 상태
    private Long currentSpeakerId; // 현재 발언자
    private String currentSpeakerConnectionId;
    private Instant currentSpeakEndTime; // 현재 발언자의 발언 종료 시각
    private Long nextSpeakerId; // 다음 발언자
    private boolean interrupted; // 끼어들기(POI) 중인지?
    private Long interruptSpeakerId; // 끼어들기(POI) 발언자
    private Instant interruptEndTime;//끼어들기(POI) 발언 종료 시각
    private final Set<PlayingUser> participants = new CopyOnWriteArraySet<>(); // 토론 참여자 리스트

    public static PlayingState init(Long debateId, List<PlayingUser> participants) {
        PlayingState playingState = PlayingState.builder()
                .debateId(debateId)
                .participants(participants)
                .build();
        playingState.setDefaultState(PlayingStatus.READY);
        return playingState;
    }

    @Builder
    private PlayingState(Long debateId,
                         PlayingStatus status,
                         Long currentSpeakerId,
                         String currentSpeakerConnectionId,
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
        this.currentSpeakerConnectionId = currentSpeakerConnectionId;
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

    public boolean participate(Long userId) {
        log.debug("participate: userId = {}", userId);
        stateLock.lock();
        try {
            Optional<PlayingUser> optional = participants.stream()
                    .filter(player -> player.getUserId().equals(userId))
                    .findFirst();
            if (optional.isPresent()) {
                sequence.getAndIncrement();
                optional.get().setParticipant(true);
                return true;
            }
            return false;
        } finally {
            stateLock.unlock();
        }
    }

    public boolean start(int expectedUserCount) {
        stateLock.lock();
        try {
            if (!canStart(expectedUserCount)) {
                log.debug("start: can not start with expectedUserCount = {}", expectedUserCount);
                return false;
            }
            this.status = PlayingStatus.STARTED;
            this.currentSpeakEndTime = Instant.now().plusSeconds(waitingTimeSeconds);
            sequence.getAndIncrement();
            return true;
        } finally {
            stateLock.unlock();
        }
    }

    private boolean canStart(int expectedCount) {
        long count = participants.stream().filter(PlayingUser::isParticipant).count();
        log.debug("participantCount = {}", count);
        return !isStarted() && count == expectedCount;
    }

    private boolean isStarted() {
        return status != PlayingStatus.READY;
    }

    // todo 업데이트 순서에 맞는 지 검증
    public boolean skip(Long userId) {
        stateLock.lock();
        try {
            if (!canSkip(userId)) {
                return false;
            }
            update();
            return true;
        } finally {
            stateLock.unlock();
        }
    }

    private boolean canSkip(Long userId) {
        return currentSpeakerId.equals(userId) && status == PlayingStatus.SPEECHING;
    }

    public void update() {
        stateLock.lock();
        try {
            sequence.getAndIncrement();
            log.debug("Debate {}: turn {} update state", debateId, turnCounter);
            try {
                setNextState();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("업데이트 예외 발생: {}", e.getMessage());
            }
        } finally {
            stateLock.unlock();
        }
    }

    private void setNextState() {
        // READY -> STARTED -> PREPARING -> IN_PROGRESS -> WAITING -> IN_PROGRESS -> ... -> FINISHED
        switch (status){
//            case READY ->{
//                Long currentSpeakerId = this.currentSpeakerId;
//                Long nextSpeakerId = this.nextSpeakerId;
//                setDefaultState(PlayingStatus.SPEECHING);
//                setState(currentSpeakerId, nextSpeakerId, speechingTimeSeconds);
//            }
            case STARTED ->{
//                Long currentSpeakerId = this.currentSpeakerId;
//                Long nextSpeakerId = this.nextSpeakerId;
                setDefaultState(PlayingStatus.PREPARING);
                setState(currentSpeakerId, nextSpeakerId, preparingTimeSeconds);
            }
            case PREPARING -> {
                Long currentSpeakerId = findFirstSpeaker().getUserId();
                Long nextSpeakerId = findNextSpeakerId(currentSpeakerId);
                setDefaultState(PlayingStatus.WAITING);
                setState(currentSpeakerId, nextSpeakerId, waitingTimeSeconds);
            }
            case WAITING ->{
                Long currentSpeakerId = this.currentSpeakerId;
                Long nextSpeakerId = this.nextSpeakerId;
                setDefaultState(PlayingStatus.SPEECHING);
                setState(currentSpeakerId, nextSpeakerId, speechingTimeSeconds);
            }
            case SPEECHING -> {
                if(turnCounter++ < maxTurnCount) {
                    log.debug("Debate {}: turn {} update state", debateId, turnCounter);
                    Long currentSpeakerId = this.nextSpeakerId;
                    Long nextSpeakerId = this.findNextSpeakerId(currentSpeakerId);
                    setDefaultState(PlayingStatus.WAITING);
                    setState(currentSpeakerId, nextSpeakerId, waitingTimeSeconds);
                }else{
                    log.debug("Debate {}: finished", debateId);
                    setDefaultState(PlayingStatus.FINISHED);
                }
            }
            case FINISHED ->{
                // Do Nothing
            }
        }
    }

    private void setState(Long currentSpeakerId, Long nextSpeakerId, int seconds) {
        this.currentSpeakerId = currentSpeakerId;
        this.currentSpeakerConnectionId = findConnectionId(currentSpeakerId);
        this.nextSpeakerId = nextSpeakerId;
        this.currentSpeakEndTime = Instant.now().plusSeconds(seconds);
    }

    private String findConnectionId(Long currentSpeakerId) {
        if (currentSpeakerId == null) {
            return null;
        }
        Optional<PlayingUser> optional = participants.stream().filter(p -> p.getUserId().equals(currentSpeakerId)).findFirst();
        return optional.orElseThrow(()->new PlayingException(HttpStatus.INTERNAL_SERVER_ERROR, "오픈비두 유저 커넥션이 존재하지 않습니다."))
                .getConnection().getConnectionId();
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
                .orElseThrow(() -> new IllegalStateException("토론의 첫 발언자를 찾을 수 없습니다"));
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

    public boolean checkSequence(int sequence) {
        stateLock.lock();
        try {
            return this.sequence.get() == sequence;
        }finally {
            stateLock.unlock();
        }
    }
}
