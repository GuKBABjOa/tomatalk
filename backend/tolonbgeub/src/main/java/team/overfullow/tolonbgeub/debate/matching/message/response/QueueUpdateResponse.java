package team.overfullow.tolonbgeub.debate.matching.message.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class QueueUpdateResponse {
    private int waitingUserCount;
}
