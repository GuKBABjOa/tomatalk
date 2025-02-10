package team.overfullow.tolonbgeub.user.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public record UserResponse(Long userId,
                            String nickname,
                            LocalDateTime createdAt,
                            LocalDateTime lastModifiedAt) {

    @Builder
    public UserResponse{

    }
}
