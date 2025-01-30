package team.overfullow.tolonbgeub.auth.jwt;

import lombok.Builder;

@Builder
public record SignedJwt(String value, Long expirySeconds) {
}
