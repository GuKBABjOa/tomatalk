package team.overfullow.tolonbgeub.game.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameMessage {
    private MessageType type;
    private Object payload;
}

