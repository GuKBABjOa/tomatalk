package team.overfullow.tolonbgeub.debate.matching.event;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.Destination;
import team.overfullow.tolonbgeub.debate.matching.message.MatchingQueueUpdateMessage;

public record MatchingQueueUpdateEvent(
        Category category,
        MatchingQueueUpdateMessage payload) {

    @Builder
    public MatchingQueueUpdateEvent {
    }

    public String destination() {
        return String.format(Destination.MATCHING.getFormat(), category.name());
    }
}