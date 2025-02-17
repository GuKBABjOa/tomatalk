package team.overfullow.tolonbgeub.auth.dto.response;

import lombok.Builder;

public record AuthResponse(String userId,
                           String accessToken,
                           String refreshToken) {

    @Builder
    public AuthResponse{
    }
}
