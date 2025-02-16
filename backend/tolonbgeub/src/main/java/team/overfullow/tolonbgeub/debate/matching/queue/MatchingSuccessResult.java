package team.overfullow.tolonbgeub.debate.matching.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.overfullow.tolonbgeub.debate.Category;

import java.util.List;

@Getter
@AllArgsConstructor
public class MatchingSuccessResult {
    private Category category;
    private List<Long> matchedUserIds;
}
