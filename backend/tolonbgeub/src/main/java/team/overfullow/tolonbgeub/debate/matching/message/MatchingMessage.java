package team.overfullow.tolonbgeub.debate.matching.message;

import lombok.Builder;

public record MatchingMessage<T>(
        MatchingMessageType messageType,
        T payload
) {
    @Builder
    public MatchingMessage {
    }
}
