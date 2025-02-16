package team.overfullow.tolonbgeub.debate.matching.event;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.Destination;
import team.overfullow.tolonbgeub.debate.matching.message.MatchingMessage;
import team.overfullow.tolonbgeub.debate.matching.message.response.MatchingSuccessResponse;

public record MatchingSuccessEvent(
     Long userId,
     MatchingMessage<MatchingSuccessResponse> payload){

    @Builder
    public MatchingSuccessEvent {
    }

    public String destination() {
        return String.format(Destination.MATCHING_USER.getFormat(), userId);
    }
}