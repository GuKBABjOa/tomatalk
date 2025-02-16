package team.overfullow.tolonbgeub.debate.matching.tmp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingSuccessMessage {
    private String connectionToken;
}
