package team.overfullow.tolonbgeub.user.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public record UserResponse(String userId,
                            String nickname,
                            String profileImageUrl,
                            LocalDateTime createdAt,
                            LocalDateTime lastModifiedAt) {

    @Builder
    public UserResponse{

    }
}
