package team.overfullow.tolonbgeub.debate.dto;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.DebateStatus;

public record DebateUserResponse(
        String userId,
        String nickname,
        String profileImage,
        String position,
        Integer order
) {
    @Builder
    public DebateUserResponse {
    }
}