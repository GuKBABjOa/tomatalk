package team.overfullow.tolonbgeub.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.auth.util.IdGenerator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static team.overfullow.tolonbgeub.auth.jwt.JwtClaimName.*;
import static team.overfullow.tolonbgeub.auth.jwt.JwtType.ACCESS;
import static team.overfullow.tolonbgeub.auth.jwt.JwtType.REFRESH;

@Slf4j
@Component
public class JwtProvider {
    private final String issuer;
    private final Map<JwtType, Long> expirySecondsMap;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final Random randomSalt;
    private final IdGenerator idGenerator;

    public JwtProvider(JwtConfigProps jwtConfigProps, IdGenerator idGenerator) {
        this.issuer = jwtConfigProps.issuer;
        this.expirySecondsMap = Map.of(
                ACCESS, jwtConfigProps.expirySeconds,
                REFRESH, jwtConfigProps.refreshExpirySeconds);
        this.algorithm = Algorithm.HMAC512(jwtConfigProps.secret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        this.randomSalt = new Random();
        this.idGenerator = idGenerator;
    }

    public SignedJwt generate(String userId, List<String> roles, JwtType type) {
        return SignedJwt.builder()
                .value(generate(userId, roles, type, expirySecondsMap.get(type)))
                .expirySeconds(expirySecondsMap.get(type))
                .build();
    }

    private String generate(String userId, List<String> roles, JwtType type, long expirySeconds) {
        String tokenId = idGenerator.generate().toString();
        log.info("Generated id {}", tokenId);
        return JWT.create().withIssuer(issuer)
                .withClaim(TOKEN_ID.claim(), tokenId)
                .withClaim(USER_ID.claim(), userId)
                .withClaim(ROLES.claim(), roles)
                .withClaim(TYPE.claim(), type.name())
                .withClaim(SALT.claim(), randomSalt.nextInt())
                .withExpiresAt(Instant.now().plus(expirySeconds, ChronoUnit.SECONDS))
                .sign(algorithm);
    }

    protected com.auth0.jwt.interfaces.JWTVerifier getVerifier() {
        return jwtVerifier;
    }
}
