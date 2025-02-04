package team.overfullow.tolonbgeub.game.message.payload;

import lombok.Builder;

@Builder
public record Greeting(String greetingMessage, String connectionToken) {
}
