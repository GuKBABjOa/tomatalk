package team.overfullow.tolonbgeub.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import team.overfullow.tolonbgeub.user.Repository.UserRepository;
import team.overfullow.tolonbgeub.user.UserException;
import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.dto.UserResponse;
import team.overfullow.tolonbgeub.user.User;

import team.overfullow.tolonbgeub.auth.util.IdGenerator;
import team.overfullow.tolonbgeub.user.dto.UserUpdateRequest;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String IMAGE_URL = "%s/api/users/%s/image";
    private final UserRepository userRepository;
    private final IdGenerator idGenerator;

    @Value("${app.image.base-url}")
    private String imageBaseUrl;

    @Override
    public UserResponse createUser(UserRequest request) {
        var userBuilder = User.builder()
                .email(request.email())
                .nickname(request.nickname());
        Optional<User> optionalUser = userRepository.findByEmail(request.email());
        if(optionalUser.isPresent()) {
            throw new UserException(HttpStatus.CONFLICT, "이미 존재하는 회원 이메일입니다.");
        } else if(StringUtils.isBlank(request.nickname())){
            userBuilder.nickname(UUID.randomUUID().toString()); // UUID로 랜덤 별명 지정
        } else if (checkNickname(request.nickname())) {
            throw new UserException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        User user = userRepository.save(userBuilder
                .id(idGenerator.generate())
                .build());
        log.info("유저 생성 성공 id = {}, email = {}", user.getId(), user.getEmail());
        return mapUserResponse(user);
    }

    @Override
    public UserResponse getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .map(toUserResponse())
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다"));
    }

    @Deprecated
    @Override
    public UserResponse getMyProfile(Long userId) {
        return userRepository.findById(userId)
                .map(toUserResponse())
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다"));
    }

    @Override
    public UserResponse updateUserProfile(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다"));

        // 닉네임 수정 (StringUtils의 isBlank()메서드 사용)
        if (StringUtils.isNotBlank(userUpdateRequest.nickname())) {
            if (checkNickname(userUpdateRequest.nickname())) {
                throw new UserException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
            }
            user.changeNickname(userUpdateRequest.nickname());
        }

        // 프로필 이미지 수정 (StringUtils의 isBlank()메서드 사용)
        if (StringUtils.isNotBlank(userUpdateRequest.profileImage())) {
            user.updateProfileImage(decodeBase64(userUpdateRequest.profileImage()));
        }

        userRepository.save(user);

        return mapUserResponse(user);
    }


    public byte[] getProfileImage(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다."));

        return user.getProfileImage(); //
    }

    @Override
    public boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Deprecated
    @Override
    public UserResponse updateUserNickname(Long userId, String newNickname) {
        // 닉네임 중복 검사
        if (checkNickname(newNickname)) {
            throw new UserException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다"));

        user.changeNickname(newNickname);
        return mapUserResponse(user);
    }

    @Override
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(toUserResponse());
    }

    private Function<User, UserResponse> toUserResponse() {
        return u -> mapUserResponse(u);
    }

    private UserResponse mapUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId().toString())
                .nickname(user.getNickname())
                .profileImageUrl((user.getProfileImage() != null) ? String.format(IMAGE_URL, imageBaseUrl, user.getId()) : null)
                .createdAt(user.getCreatedAt())
                .lastModifiedAt(user.getLastModifiedAt())
                .build();
    }

    private static String encodeBase64(byte[] data) {
        return (data != null) ? Base64.getEncoder().encodeToString(data) : null;
    }


    private byte[] decodeBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }
}
