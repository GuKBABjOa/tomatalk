package team.overfullow.tolonbgeub.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.overfullow.tolonbgeub.auth.blacklist.TokenBlackList;
import team.overfullow.tolonbgeub.auth.dto.AuthTokens;
import team.overfullow.tolonbgeub.auth.dto.KakaoLogin;
import team.overfullow.tolonbgeub.auth.dto.Login;
import team.overfullow.tolonbgeub.auth.dto.Logout;
import team.overfullow.tolonbgeub.auth.jwt.JwtClaims;
import team.overfullow.tolonbgeub.auth.jwt.JwtType;
import team.overfullow.tolonbgeub.auth.oauth.kakao.KakaoLoginService;
import team.overfullow.tolonbgeub.auth.oauth.OauthProvider;
import team.overfullow.tolonbgeub.auth.security.JwtAuthenticationToken;
import team.overfullow.tolonbgeub.auth.oauth.kakao.KakaoAuthenticationToken;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final AuthTokensProvider authTokensProvider;
    private final AuthenticationManager authenticationManager;
    private final KakaoLoginService kakaoLoginService;
    private final TokenBlackList tokenBlackList;

    public String getLoginUrl(OauthProvider oauthProvider) {
        return switch (oauthProvider) {
            case KAKAO -> kakaoLoginService.getLoginUrl();
            default -> throw new AuthException(HttpStatus.INTERNAL_SERVER_ERROR, "유효하지 않은 인증 제공자");
        };
    }

    @Transactional
    public AuthTokens login(Login login) {
        log.info("login process in progress : {}", login);
        Authentication unauthenticated;
        if (login instanceof KakaoLogin kakaoLogin) {
            log.info("카카오 로그인");
            unauthenticated = KakaoAuthenticationToken.unauthenticated(kakaoLogin.getCode());
        }else{
            throw new AuthException(HttpStatus.INTERNAL_SERVER_ERROR, "지원하지 않는 타입의 로그인 요청");
        }

        log.info("로그인 검증 진행");
        return authTokensProvider.generate(authenticationManager.authenticate(unauthenticated));
    }

    @Transactional
    public void logout(Logout logout) {
        log.debug("logout process in progress : {}",logout);
        Authentication validated = validate(logout.accessToken(), JwtType.ACCESS);
//        validate(logout.refreshToken(), JwtType.REFRESH);
        JwtClaims claims = JwtClaims.class.cast(validated.getDetails());
        tokenBlackList.add(claims.tokenId(), claims.expiresAt());
        log.info("로그아웃 성공");
    }

    private Authentication validate(String token, JwtType type) {
        var unauthenticated = JwtAuthenticationToken.unauthenticated(token);
        var jwtAuthentication = (JwtAuthenticationToken) authenticationManager.authenticate(unauthenticated);

        if(!jwtAuthentication.isAuthenticated()){
            throw new AuthException(HttpStatus.UNAUTHORIZED, "인증 ");
        }

        validate(jwtAuthentication.getDetails(), type);
        return jwtAuthentication;
    }

    private void validate(JwtClaims claims, JwtType type) {
        if(claims.type() != type){
            throw new AuthException(HttpStatus.UNAUTHORIZED,"유효하지 않은 타입의 토큰입니다");
        }

        if (tokenBlackList.exists(claims.tokenId())) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "이미 사용된 토큰입니다");
        }
    }
}
