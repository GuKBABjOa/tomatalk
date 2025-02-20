package team.overfullow.tolonbgeub.debate.debate.dto;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.playing.message.response.PlayingUserResponse;

import java.util.List;

public record DebateRoomResponse(
        String openviduToken,
        String debateId,
        String category,
        String subject,
        Boolean participant,
        List<PlayingUserResponse> users
){
    @Builder
    public DebateRoomResponse {
    }

    public static DebateRoomResponse fromDto(DebateRoomDto dto, List<PlayingUserResponse> users) {
        return DebateRoomResponse.builder()
                .openviduToken(dto.openviduToken())
                .debateId(dto.debateId())
                .category(dto.category())
                .subject(dto.subject())
                .participant(dto.participant())
                .users(users)
                .build();

    }
}