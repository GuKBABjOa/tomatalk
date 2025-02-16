package team.overfullow.tolonbgeub.debate.playing.event;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.playing.message.PlayingMessage;
import team.overfullow.tolonbgeub.debate.Destination;
import team.overfullow.tolonbgeub.debate.playing.message.response.PlayingStateResponse;

public record StateUpdateEvent(
        Long debateId,
        PlayingMessage<PlayingStateResponse> payload) {

    @Builder
    public StateUpdateEvent {
    }

    public String destination() {
        return String.format(Destination.DEBATE.getFormat(), debateId);
    }
}