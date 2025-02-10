package team.overfullow.tolonbgeub.user.service;

import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.dto.UserResponse;

public interface UserService {
    // 유저 생성
    String createUser(UserRequest request);

    // 유저 프로필 조회
    UserResponse getUserProfile(Long userId);

    // 내 프로필 조회
    UserResponse getMyProfile(Long userId);

    // 닉네임 중복 체크 (boolean 반환)
    boolean checkNickname(String nickname);

    // 사용자 정보 수정 (닉네임 변경)
    UserResponse updateUserNickname(Long userId, String newNickname);
}
