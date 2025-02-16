package team.overfullow.tolonbgeub.debate.playing.message.payload.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.debate.playing.PlayingUser;

@Data
@NoArgsConstructor
public class PlayingUserResponse {
    private String userId;
    private String nickname;
    private String profileImageUrl;
    private String position;
    private Integer order;
    private boolean participant;

    @Builder
    private PlayingUserResponse(String position, String userId, String nickname, String profileImageUrl, Integer order, boolean participant) {
        this.position = position;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.order = order;
        this.participant = participant;
    }

    public static PlayingUserResponse fromDomain(PlayingUser user) {
        return PlayingUserResponse.builder()
                .position(user.getPosition())
                .userId(user.getUserId().toString())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .order(user.getPositionOrder())
                .participant(user.isParticipant())
                .build();
    }
}