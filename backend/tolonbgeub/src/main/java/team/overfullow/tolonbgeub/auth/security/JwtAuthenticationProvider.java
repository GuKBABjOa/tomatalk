package team.overfullow.tolonbgeub.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.auth.UserId;
import team.overfullow.tolonbgeub.auth.blacklist.TokenBlackList;
import team.overfullow.tolonbgeub.auth.jwt.JwtClaims;
import team.overfullow.tolonbgeub.auth.jwt.JwtValidationException;
import team.overfullow.tolonbgeub.auth.jwt.JwtValidator;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtValidator jwtValidator;
    private final TokenBlackList tokenBlackList;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var jwtAuthentication = (JwtAuthenticationToken) authentication;
        var credentials = jwtAuthentication.getCredentials();
        try {
            return authenticate(credentials.jwt());
        } catch (JwtValidationException e) {
            log.warn("토큰 검증 실패 = {}", e.getMessage());
            return JwtAuthenticationToken.unauthenticated(credentials.jwt());
        }
    }

    private JwtAuthenticationToken authenticate(String jwt) throws JwtValidationException {
        JwtClaims claims = jwtValidator.validate(jwt);
        var authorities = claims.roles().stream().map(SimpleGrantedAuthority::new).toList();
        log.debug("토큰 검증 claims = {}, 성공: 권한 = {}", claims, authorities);
        log.info("claims = {}", claims);
        String tokenId = claims.tokenId();
        if (!isNull(tokenId) && tokenBlackList.exists(tokenId)) {
            throw new JwtValidationException("블랙 리스트에 등록된 토큰입니다.");
        }

        return JwtAuthenticationToken.authenticated(new UserId(claims.userId()), jwt, claims, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }
}
