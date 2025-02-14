package team.overfullow.tolonbgeub.debate.dto;

import lombok.Builder;

import java.util.List;

public record DebateRoomResponse(
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