package team.overfullow.tolonbgeub.debate.matching.message.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import team.overfullow.tolonbgeub.debate.Category;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingCancelResponse {
    private String userId;
    private Category category;
}
