package team.overfullow.tolonbgeub.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.overfullow.tolonbgeub.auth.UserId;
import team.overfullow.tolonbgeub.user.dto.UserResponse;
import team.overfullow.tolonbgeub.user.service.UserService;
import team.overfullow.tolonbgeub.user.dto.UserRequest;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 유저 프로필 조회 API
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserProfile(
            @AuthenticationPrincipal UserId principal){
        return ResponseEntity.ok(userService.getMyProfile(Long.parseLong(principal.value())));
    }

    // 닉네임 중복 조회
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(userService.checkNickname(nickname));
    }

    //  유저 생성 API
    @Deprecated
    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }


//    // 닉네임 변경
    @PutMapping("/change/{userId}")
    public ResponseEntity<UserResponse> changeNickname(
            @AuthenticationPrincipal UserId principal,
            @PathVariable Long userId,
            @RequestBody UserRequest request) {
        if(!Objects.equals(userId,Long.parseLong(principal.value()))){
            throw new UserException(HttpStatus.FORBIDDEN, "권한이 없습니다");
        }
        return ResponseEntity.ok(userService.updateUserNickname(userId, request.nickname()));
    }
}
