package team.overfullow.tolonbgeub.debate.playing.message.response;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.playing.state.PlayingStatus;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record PlayingStateResponse(
        int sequence,                   // 상태 변경 순서
        PlayingStatus status,           // 현재 토론 상태
        String currentSpeakerId,        // 현재 발언자
        Instant currentSpeakEndTime,    // 현재 발언자의 발언 종료 시각
        String nextSpeakerId,            // 다음 발언자
        boolean canInterrupt,            // 끼어들기(POI)가능 여부
        boolean isInterrupted,           // 끼어들기(POI) 중인지?
        String interruptSpeakerId,       // 끼어들기(POI) 발언자
        Instant interruptEndTime,           //끼어들기(POI) 발언 종료 시각
        List<PlayingUserResponse> participants // 토론 참여자 리스트
) {
    // 생성자에서 유효성 검증
    @Builder
    public PlayingStateResponse {
        participants = List.copyOf(participants); // defensive copy
        Objects.requireNonNull(status, "Status must not be null");
    }
}
