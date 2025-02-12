package team.overfullow.tolonbgeub.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.overfullow.tolonbgeub.auth.dto.AuthTokens;
import team.overfullow.tolonbgeub.auth.dto.KakaoLogin;
import team.overfullow.tolonbgeub.auth.dto.Logout;
import team.overfullow.tolonbgeub.auth.dto.response.AuthResponse;
import team.overfullow.tolonbgeub.auth.dto.response.LoginUrlResponse;
import team.overfullow.tolonbgeub.auth.oauth.OauthProvider;
import team.overfullow.tolonbgeub.auth.resolver.AccessToken;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/kakao/url")
    public ResponseEntity<LoginUrlResponse> getLoginUrlWithKakao() {
        return ResponseEntity.ok(new LoginUrlResponse(authService.getLoginUrl(OauthProvider.KAKAO)));
    }

    // 프론트 테스트용
    @Deprecated
    @GetMapping("/login/kakao/test")
    public ResponseEntity<AuthResponse> loginWithKakaoMockClient(@Valid @NotBlank @RequestParam("code") String code) {
        return loginWithKakao(code);
    }

    @PostMapping("/login/kakao")
    public ResponseEntity<AuthResponse> loginWithKakao(@Valid @NotBlank @RequestParam("code") String code) {
        log.info("POST 카카오 로그인 요청 code = {}", code);

        AuthTokens authTokens = authService.login(KakaoLogin.builder()
                .code(code)
                .build());

        log.info("kakao login success");
        return generateAuthResponse(authTokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserId userId, @AccessToken @NotNull String accessToken) {
        log.info("logout request userId = {}, accessToken = {}", userId.value(), accessToken);
        authService.logout(Logout.builder()
                .accessToken(accessToken)
//                .refreshToken(refreshToken)
                .build());

        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<AuthResponse> refresh(@RequestBody AuthRequestDto request) {
//        log.debug("refresh request = {}", request.refreshToken());
//        AuthTokens authTokens = authService.refresh(request.refreshToken());
//        return generateAuthResponse(authTokens);
//    }

    private static ResponseEntity<AuthResponse> generateAuthResponse(AuthTokens authTokens) {
//        String refreshCookie = generateRefreshCookie(authTokens.forRefresh());
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.SET_COOKIE, refreshCookie)
                .body(AuthResponse.builder()
                        .accessToken(authTokens.forAccess().value())
//                        .refreshToken(authTokens.forRefresh().value())
                        .refreshToken("고도화 과정에서 추가하겠습니다")
                        .build());
    }

//    private static String generateRefreshCookie(SignedJwt refreshToken) {
//        return ResponseCookie
//                .from("refreshToken", refreshToken.value())
//                .domain(null) // 도메인 생략
//                .path("/") // 최상위 경로
//                .httpOnly(true)
//                .secure(true) // HTTPS 연결에서만 쿠키를 전송
//                .maxAge(refreshToken.expirySeconds())
//                .sameSite("None") // 크로스 오리진 요청 허용
//                .build().toString();
//    }

    /**
     * just use for test
     */
    @Deprecated
    @GetMapping("/authentication")
    public ResponseEntity<String> authentication(@AuthenticationPrincipal UserId userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userId.value().toString());
    }

    /**
     * just use for test
     */
    @Deprecated
    @GetMapping("/authorization")
    public ResponseEntity<String> authorization() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("인가 테스트 성공");
    }
}
