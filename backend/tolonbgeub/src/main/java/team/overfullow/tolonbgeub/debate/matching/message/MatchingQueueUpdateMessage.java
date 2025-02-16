package team.overfullow.tolonbgeub.debate.matching.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.debate.Category;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingQueueUpdateMessage {
    private Category category;
    private int subscriberCount;
}
