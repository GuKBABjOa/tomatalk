package team.overfullow.tolonbgeub.user.dto;

import lombok.Builder;

public record UserRequest (
                          String nickname){
    @Builder
    public UserRequest{
    }
}
