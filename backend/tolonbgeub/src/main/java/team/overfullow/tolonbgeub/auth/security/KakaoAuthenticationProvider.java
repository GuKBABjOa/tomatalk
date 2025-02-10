package team.overfullow.tolonbgeub.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.auth.UserId;
import team.overfullow.tolonbgeub.auth.UserRole;
import team.overfullow.tolonbgeub.auth.oauth.kakao.KakaoLoginService;
import team.overfullow.tolonbgeub.auth.oauth.OauthUserInfo;
import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.dto.UserResponse;
import team.overfullow.tolonbgeub.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final KakaoLoginService kakaoLoginService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("authenticate");
        var kakaoAuthenticationToken = (KakaoAuthenticationToken) authentication;
        var credentials = kakaoAuthenticationToken.getCredentials();

        String email = kakaoLoginService.login(credentials.code()).email();
        Optional<UserResponse> userOptional = userService.findByEmail(email);
        Long userId;
        if (userOptional.isPresent()) {
            userId = userOptional.get().userId();
            log.info("카카오 로그인 성공 email = {}, userId = {}", email, userId);
        } else {
            UserResponse userCreated = userService.createUser(UserRequest.builder()
                    .email(email)
                    .build());
            userId = userCreated.userId();
            log.info("카카오 회원가입 성공 email = {}, userId = {}", email, userId);
        }

        // 기본적으로 모두 User 권한
        var authorities = List.of(new SimpleGrantedAuthority(UserRole.USER.role()));
        return KakaoAuthenticationToken.authenticated(new UserId(userId.toString()), authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(KakaoAuthenticationToken.class);
    }
}