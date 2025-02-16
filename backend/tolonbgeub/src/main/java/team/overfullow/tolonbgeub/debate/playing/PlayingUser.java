package team.overfullow.tolonbgeub.debate.playing;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public class PlayingUser {
    private final Long userId;
    private final String nickname;
    private final String profileImageUrl;
    private final String position;
    @Min(1)
    private final Integer positionOrder; //todo 변경 사항 공유
    @Min(1)
    private final Integer speechOrder;

    @Setter
    private volatile boolean participant;

    @Builder
    protected PlayingUser(String position, Long userId, String nickname, String profileImageUrl, Integer positionOrder, Integer speechOrder, boolean participant) {
        this.userId = Objects.requireNonNull(userId);
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.position = Objects.requireNonNull(position);
        this.positionOrder = Objects.requireNonNull(speechOrder);;
        this.speechOrder = Objects.requireNonNull(speechOrder);
        this.participant = participant;
    }
}

/*
{
    "messageType":"STATE_UPDATE",
    "payload":{
        "status":"IN_PROGRESS",
        "currentSpeakerId":"3451001927368704003",
        "currentSpeakEndTime":"2025-02-15T11:38:44.658297Z",
        "nextSpeakerId":"3458561069809664004",
        "canInterrupt":true,
        "isInterrupted":false,
        "interruptSpeakerId":null,
        "interruptEndTime":null,
        "participants":[
            {
                "userId":"3337391452454912001",
                "nickname":"user0",
                "profileImageUrl":"제공 예정",
                "position":"찬성",
                "order":1,
                "participant":true
            },
            {
                "userId":"3442944568721408002",
                "nickname":"user1",
                "profileImageUrl":"제공 예정",
                "position":"찬성",
                "order":2,
                "participant":false
            },
            {
                "userId":"3451001927368704003",
                "nickname":"user2"
                ,"profileImageUrl":"제공 예정",
                "position":"반대",
                "order":3,
                "participant":false
            },
            {
                "userId":"3458561069809664004",
                "nickname":"user3",
                "profileImageUrl":"제공 예정",
                "position":"반대",
                "order":4,
                "participant":false
            }
        ]
    }
}
 */