package team.overfullow.tolonbgeub.matching;

import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.DebateRule;

public record MatchingJoinRequest(String userId,
                                  DebateRule debateRule,
                                  Category category){
}