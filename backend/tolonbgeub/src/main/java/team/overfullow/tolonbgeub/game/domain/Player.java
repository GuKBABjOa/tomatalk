package team.overfullow.tolonbgeub.game.domain;

import lombok.Data;

@Data
public class Player {
    private String userId;
    private String stance;
    private boolean isTurn;
    private boolean winner;
}