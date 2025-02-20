package team.overfullow.tolonbgeub.debate.playing.state;

import com.google.errorprone.annotations.ThreadSafe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateUserResponse;
import team.overfullow.tolonbgeub.debate.debate.service.DebateService;
import team.overfullow.tolonbgeub.debate.playing.PlayingException;
import team.overfullow.tolonbgeub.debate.playing.message.response.PlayingStateResponse;
import team.overfullow.tolonbgeub.debate.playing.message.response.PlayingUserResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ThreadSafe
@RequiredArgsConstructor
public class PlayingStateManager {
    private final Map<Long, PlayingState> states = new ConcurrentHashMap<>();
    private final DebateService debateService;

    public Optional<PlayingStateResponse> join(Long debateId, Long userId) {
        PlayingState state =getPlayingState(debateId);
        boolean participated = state.participate(userId);
        if (participated) {
            log.debug("join user {}", userId);
            return Optional.of(toStateUpdate(state));
        }
        return Optional.empty();
    }

    public synchronized PlayingState init(Long debateId) {
        List<DebateUserResponse> users = debateService.getRoomById(debateId, null).users();
        var playingUsers = users.stream()
                .map(u ->
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
        return states.computeIfAbsent(debateId, k -> PlayingState.init(k, playingUsers));
    }

    public Optional<PlayingStateResponse> start(Long debateId) {
        PlayingState state = getPlayingState(debateId);
        boolean started = state.start(4);
        if (started) {
            debateService.start(debateId);
            return Optional.of(toStateUpdate(state));
        }
        return Optional.empty();
    }

    public Optional<PlayingStateResponse> skip(Long debateId, Long userId) {
        PlayingState state = getPlayingState(debateId);
        boolean skipped = state.skip(userId);
        if (skipped) {
            log.debug("skip speech of {}", userId);
            return Optional.of(toStateUpdate(state));
        }
        return Optional.empty();
    }

    public PlayingStateResponse update(Long debateId) {
        PlayingState state = getPlayingState(debateId);
        // update 순서 검증
        state.update();
        return toStateUpdate(state);
    }

    private PlayingState getPlayingState(Long debateId) {
        return states.computeIfAbsent(debateId, (k) -> {
            throw new PlayingException(HttpStatus.NOT_FOUND, "Playing Exception: 진행중이지 않은 토론 id, "+debateId);
        });
    }

    private static PlayingStateResponse toStateUpdate(PlayingState state) {
        return PlayingStateResponse.builder()
                .sequence(state.getSequence().get())
                .status(state.getStatus())
                .currentSpeakerId(toStringOrNull(state.getCurrentSpeakerId()))
                .currentSpeakerConnectionId(toStringOrNull(state.getCurrentSpeakerConnectionId()))
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

    public boolean isLatestSequence(Long debateId, PlayingStateResponse currentState) {
        return getPlayingState(debateId).checkSequence(currentState.sequence());
    }

    public int getActiveParticipantCount(Long debateId) {
        return (int)getPlayingState(debateId).getParticipants().stream().filter(PlayingUser::isParticipant).count();
    }
}
