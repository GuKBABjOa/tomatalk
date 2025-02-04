package team.overfullow.tolonbgeub.game.message.payload;

import lombok.Builder;

@Builder
public record CountDown(Integer totalTime, Integer remainingTime) {
}
