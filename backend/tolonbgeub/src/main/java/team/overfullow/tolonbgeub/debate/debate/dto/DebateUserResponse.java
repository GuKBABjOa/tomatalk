package team.overfullow.tolonbgeub.debate.debate.dto;

import lombok.Builder;

public record DebateUserResponse(
        String userId,
        String nickname,
        String profileImageUrl,
        String position,
        Integer positionOrder,
        Integer speechOrder
) {
    @Builder
    public DebateUserResponse {
    }
}