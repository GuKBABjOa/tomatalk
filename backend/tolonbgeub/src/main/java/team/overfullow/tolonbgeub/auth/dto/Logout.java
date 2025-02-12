package team.overfullow.tolonbgeub.auth.dto;

import lombok.Builder;

public record Logout(String accessToken, String refreshToken) {
    @Builder
    public Logout{}
}
