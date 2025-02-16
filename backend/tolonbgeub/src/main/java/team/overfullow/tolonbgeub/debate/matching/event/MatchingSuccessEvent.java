package team.overfullow.tolonbgeub.debate.matching.event;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.Destination;
import team.overfullow.tolonbgeub.debate.matching.tmp.MatchingSuccessMessage;

public record MatchingSuccessEvent (
     Long userId,
     MatchingSuccessMessage payload){

    @Builder
    public MatchingSuccessEvent {
    }

    public String destination() {
        return String.format(Destination.MATCHING_USER.getFormat(), userId);
    }
}