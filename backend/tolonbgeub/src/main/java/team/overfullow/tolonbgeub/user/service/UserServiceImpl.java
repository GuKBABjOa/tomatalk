package team.overfullow.tolonbgeub.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import team.overfullow.tolonbgeub.user.Repository.UserRepository;
import team.overfullow.tolonbgeub.user.UserException;
import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.dto.UserResponse;
import team.overfullow.tolonbgeub.user.User;

import team.overfullow.tolonbgeub.auth.util.IdGenerator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private  final IdGenerator idGenerator;

    // user 생성
    // 일단 id 확인용으로 저장, 출력하고 값을 할당
    @Override
    public String createUser(UserRequest request) {
        // 닉네임 중복 검사
        if (checkNickname(request.nickname())) {
            throw new UserException(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다.");
        }

        try {
            long id = idGenerator.generate();
            System.out.println("userId: " + id);

            User user = User.builder()
                    .id(id)
                    .nickname(request.nickname())
                    .build();

            userRepository.save(user);
            return "사용자 생성 성공! ID: " + id;

        } catch (Exception e) {
            throw new UserException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 생성 중 오류 발생: " + e.getMessage());
        }
    }


    @Override
    public UserResponse getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .map(u -> UserResponse.builder()
                        .userId(u.getId())
                        .nickname(u.getNickname())
                        .createdAt(u.getCreatedAt())
                        .lastModifiedAt(u.getLastModifiedAt())
                        .build())
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND));
    }

    @Override
    public UserResponse getMyProfile(Long userId) {
        return userRepository.findById(userId)
                .map(u -> UserResponse.builder()
                        .userId(u.getId())
                        .nickname(u.getNickname())
                        .createdAt(u.getCreatedAt())
                        .lastModifiedAt(u.getLastModifiedAt())
                        .build())
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND));
    }


    @Override
    public boolean  checkNickname(String nickname) {
        return userRepository.countByNickname(nickname) > 0; // 닉네임이 존재하면 true 반환
    }

    @Override
    public UserResponse updateUserNickname(Long userId ,String newNickname) {
        // 닉네임 중복 검사
        if (checkNickname(newNickname)) {
            throw new UserException(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다.");
        }

        return userRepository.findById(userId)
                .map(user -> {
                    // 닉네임 변경
                    User updatedUser = User.builder()
                            .id(user.getId()) // 기존 ID 유지
                            .nickname(newNickname) // 새로운 닉네임 적용
                            .build();

                    userRepository.save(updatedUser);

                    return UserResponse.builder()
                            .userId(updatedUser.getId())
                            .nickname(updatedUser.getNickname())
                            .createdAt(updatedUser.getCreatedAt())
                            .lastModifiedAt(updatedUser.getLastModifiedAt())
                            .build();
                })
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

}
