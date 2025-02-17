package team.overfullow.tolonbgeub.debate.matching.message.response;

import lombok.*;
import team.overfullow.tolonbgeub.debate.Category;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class QueueUpdateResponse {
    private Category category;
    private int waitingUserCount;
}
