package team.overfullow.tolonbgeub.auth.dto;

import lombok.Builder;
import team.overfullow.tolonbgeub.auth.UserId;
import team.overfullow.tolonbgeub.auth.jwt.SignedJwt;

@Builder
public record AuthTokens(
        UserId userId,
        SignedJwt forAccess,
        SignedJwt forRefresh) {
}
