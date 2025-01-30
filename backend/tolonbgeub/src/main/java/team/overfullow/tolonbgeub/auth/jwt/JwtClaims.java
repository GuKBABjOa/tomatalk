package team.overfullow.tolonbgeub.auth.jwt;

import java.time.Instant;
import java.util.List;

public record JwtClaims(String tokenId,
                        String userId,
                        JwtType type,
                        List<String> roles,
                        Instant expiresAt) {
}