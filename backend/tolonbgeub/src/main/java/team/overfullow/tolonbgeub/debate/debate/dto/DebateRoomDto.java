package team.overfullow.tolonbgeub.debate.debate.dto;

import lombok.Builder;

import java.util.List;

public record DebateRoomDto(
        String openviduToken,
        String debateId,
        String category,
        String subject,
        Boolean participant,
        List<DebateUserDto> users
){
    @Builder
    public DebateRoomDto {
    }
}