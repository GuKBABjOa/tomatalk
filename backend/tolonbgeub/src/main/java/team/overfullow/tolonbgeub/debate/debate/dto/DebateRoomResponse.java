package team.overfullow.tolonbgeub.debate.debate.dto;

import lombok.Builder;

import java.util.List;

public record DebateRoomResponse(
        String openviduToken,
        String debateId,
        String category,
        String subject,
        Boolean participant,
        List<DebateUserResponse> users
){
    @Builder
    public DebateRoomResponse {
    }
}