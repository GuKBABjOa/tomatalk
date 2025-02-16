package team.overfullow.tolonbgeub.debate.matching.message;

import lombok.*;
import team.overfullow.tolonbgeub.debate.Category;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingQueueUpdateMessage {
    private Category category;
    private int waitingUserCount;
}
