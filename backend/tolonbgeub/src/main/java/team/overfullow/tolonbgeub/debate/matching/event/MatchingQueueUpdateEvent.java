package team.overfullow.tolonbgeub.debate.matching.event;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.Destination;
import team.overfullow.tolonbgeub.debate.matching.message.MatchingMessage;
import team.overfullow.tolonbgeub.debate.matching.message.response.QueueUpdateResponse;

public record MatchingQueueUpdateEvent(
        Long userId,
        MatchingMessage<QueueUpdateResponse> payload) {

    @Builder
    public MatchingQueueUpdateEvent {
    }

    public String destination() {
        return String.format(Destination.MATCHING_USER.getFormat(), userId);
    }
}