package team.overfullow.tolonbgeub.debate.playing.message;

import lombok.Builder;

public record DebateMessage<T>(
        DebateMessageType messageType,
        T payload
) {
    @Builder
    public DebateMessage {
    }
}
