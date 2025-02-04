package team.overfullow.tolonbgeub.game.message.payload;

import lombok.Data;

@Data
public class StanceInfo {
    private String stance;
    private boolean isTurn;
}