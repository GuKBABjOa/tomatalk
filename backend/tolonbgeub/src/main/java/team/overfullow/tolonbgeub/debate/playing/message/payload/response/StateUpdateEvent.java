package team.overfullow.tolonbgeub.debate.playing.message.payload.response;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.playing.message.DebateMessage;
import team.overfullow.tolonbgeub.debate.playing.message.Destination;

public record StateUpdateEvent(
        Long debateId,
        DebateMessage<PlayingStateResponse> payload) {

    @Builder
    public StateUpdateEvent {
    }

    public String destination() {
        return String.format(Destination.DEBATE.getFormat(), debateId);
    }
}