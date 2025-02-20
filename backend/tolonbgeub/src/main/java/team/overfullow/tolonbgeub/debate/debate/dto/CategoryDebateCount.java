package team.overfullow.tolonbgeub.debate.debate.dto;

import lombok.Getter;
import team.overfullow.tolonbgeub.debate.Category;

@Getter
public class CategoryDebateCount {
    private final Category category;
    private final Long count;

    public CategoryDebateCount(Category category, Long count) {
        this.category = category;
        this.count = count;
    }
}
