package team.overfullow.tolonbgeub.debate.playing;

import com.google.errorprone.annotations.ThreadSafe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.debate.debate.DebateService;
import team.overfullow.tolonbgeub.debate.playing.message.payload.response.PlayingStateResponse;
import team.overfullow.tolonbgeub.debate.playing.message.payload.response.PlayingUserResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ThreadSafe
@RequiredArgsConstructor
public class PlayingStateManager {
    private final Map<Long, PlayingState> states = new ConcurrentHashMap<>();
    private final DebateService debateService;

    public synchronized PlayingStateResponse join(Long debateId, Long userId) {

        // todo authenticate user
        PlayingState state = states.computeIfAbsent(debateId, k -> init(debateId));
        state.participate(userId);
        log.info("join user {}", userId);
        return toStateUpdate(state);
    }

    private PlayingState init(Long debateId) {
        var playingUsers = debateService.getRoomById(debateId, null)
                .users().stream().map(u ->
                        PlayingUser.builder()
                                .userId(Long.parseLong(u.userId()))
                                .nickname(u.nickname())
                                .position(u.position())
                                .profileImageUrl(u.profileImageUrl())
                                .positionOrder(u.positionOrder())
                                .speechOrder(u.speechOrder())
                                .participant(false)
                                .build())
                .toList();
        return PlayingState.init(debateId, playingUsers);
    }

    public synchronized PlayingStateResponse start(Long debateId) {
        PlayingState state = getPlayingState(debateId);
        state.start();
        debateService.start(debateId);
        return toStateUpdate(state);
    }

    public synchronized boolean areAllParticipantsJoined(Long debateId, int expectedCount) {
        return states.containsKey(debateId) &&
                states.get(debateId).canStart(expectedCount);
    }

    public PlayingStateResponse update(Long debateId) {
        PlayingState state = getPlayingState(debateId);
        // update 순서 검증
        state.update();
        return toStateUpdate(state);
    }

    private PlayingState getPlayingState(Long debateId) {
        return states.computeIfAbsent(debateId, (k) -> {
            throw new PlayingException(HttpStatus.NOT_FOUND, "진행중이지 않은 토론 id");
        });
    }

    private static PlayingStateResponse toStateUpdate(PlayingState state) {
        return PlayingStateResponse.builder()
                .status(state.getStatus())
                .currentSpeakerId(toStringOrNull(state.getCurrentSpeakerId()))
                .currentSpeakEndTime(state.getCurrentSpeakEndTime())
                .nextSpeakerId(toStringOrNull(state.getNextSpeakerId()))
                .canInterrupt(state.canInterrupt())
                .isInterrupted(state.isInterrupted())
                .interruptSpeakerId(toStringOrNull(state.getInterruptSpeakerId()))
                .interruptEndTime(state.getInterruptEndTime())
                .participants(state.getParticipants().stream()
                        .map(PlayingUserResponse::fromDomain)
                        .toList())
                .build();
    }

    private static String toStringOrNull(Object o) {
        return o == null ? null : o.toString();
    }
}
