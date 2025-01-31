package team.overfullow.tolonbgeub.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * just use for test
     * @param userId
     * @return
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
     * @return
     */
    @Deprecated
    @GetMapping("/authorization")
    public ResponseEntity<String> authorization() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("인가 테스트 성공");
    }
}
