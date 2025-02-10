package team.overfullow.tolonbgeub.auth.dto;

import lombok.Builder;
import team.overfullow.tolonbgeub.auth.jwt.SignedJwt;

@Builder
public record AuthTokens(SignedJwt forAccess, SignedJwt forRefresh) {
}
