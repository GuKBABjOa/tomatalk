package team.overfullow.tolonbgeub.debate.playing.message;

import lombok.Builder;

public record PlayingMessage<T>(
        PlayingMessageType messageType,
        T payload
) {
    @Builder
    public PlayingMessage {
    }
}
