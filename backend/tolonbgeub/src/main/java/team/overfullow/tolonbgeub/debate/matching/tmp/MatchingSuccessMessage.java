package team.overfullow.tolonbgeub.debate.matching.tmp;

import lombok.AllArgsConstructor;
import team.overfullow.tolonbgeub.debate.Category;

import java.util.List;

@AllArgsConstructor
public class MatchingSuccessMessage {
    private Category category;
    private List<Long> userIds;
}
