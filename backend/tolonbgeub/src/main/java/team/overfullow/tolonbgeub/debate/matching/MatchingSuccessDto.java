package team.overfullow.tolonbgeub.debate.matching;

import lombok.Builder;

import java.util.List;

@Builder
public record MatchingSuccessDto(String serverUrl,
                                 String gameId,
                                 List<String> userIds) {
}