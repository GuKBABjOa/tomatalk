package team.overfullow.tolonbgeub.user.dto;

import lombok.Builder;

public record UserUpdateRequest (String nickname, String profileImage){
    @Builder
    public UserUpdateRequest{
    }
}