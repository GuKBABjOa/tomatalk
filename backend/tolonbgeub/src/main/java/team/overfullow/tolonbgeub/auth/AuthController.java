package team.overfullow.tolonbgeub.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.overfullow.tolonbgeub.auth.dto.response.LoginUrlResponse;
import team.overfullow.tolonbgeub.auth.oauth.KakaoLoginService;
import team.overfullow.tolonbgeub.auth.oauth.KakaoTokens;
import team.overfullow.tolonbgeub.auth.oauth.KakaoUserInfo;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/login/kakao/url")
    public ResponseEntity<LoginUrlResponse> getLoginUrlWithKakao() {
        return ResponseEntity.ok(new LoginUrlResponse(kakaoLoginService.getLoginUrl()));
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<String> loginWithKakao(@RequestParam("code") String code) {
        log.info("카카오 로그인 요청 code = {}", code);
        KakaoTokens kakaoTokens = kakaoLoginService.getKakaoTokens(code);
        KakaoUserInfo userInfo = kakaoLoginService.getUserInfo(kakaoTokens.accessToken());

        log.info("kakao login successful");
        log.info("userInfo: {}", userInfo);

        return ResponseEntity.ok("로그인 성공");
    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequestDto request) {
//        log.debug("login request = {}", request);
//        AuthTokens authTokens = authService.login(Login.builder()
//                .email(request.email())
//                .password(request.password())
//                .build());
//        log.debug("login success for = {}", request);
//        return generateAuthResponse(authTokens);
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<AuthResponse> refresh(@RequestBody AuthRequestDto request) {
//        log.debug("refresh request = {}", request.refreshToken());
//        AuthTokens authTokens = authService.refresh(request.refreshToken());
//        return generateAuthResponse(authTokens);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(@AccessToken @NotNull String accessToken,
//                                       @RequestBody AuthRequestDto request) {
//        log.info("logout request: accessToken = {}", accessToken);
//        log.info("logout request: refreshToken = {}", request.refreshToken());
//        authService.logout(Logout.builder()
//                .accessToken(accessToken)
//                .refreshToken(request.refreshToken())
//                .build());
//
//        return ResponseEntity.noContent().build();
//    }
//
//    private static ResponseEntity<AuthResponse> generateAuthResponse(AuthTokens authTokens) {
////        String refreshCookie = generateRefreshCookie(authTokens.forRefresh());
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .contentType(MediaType.APPLICATION_JSON)
////                .header(HttpHeaders.SET_COOKIE, refreshCookie)
//                .body(AuthResponse.builder()
//                        .accessToken(authTokens.forAccess().value())
//                        .refreshToken(authTokens.forRefresh().value())
//                        .build());
//    }
//
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
