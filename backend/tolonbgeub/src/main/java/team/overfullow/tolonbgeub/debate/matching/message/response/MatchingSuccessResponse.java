package team.overfullow.tolonbgeub.debate.matching.message.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingSuccessResponse {
    private String debateId;
    private String openviduToken;
}
