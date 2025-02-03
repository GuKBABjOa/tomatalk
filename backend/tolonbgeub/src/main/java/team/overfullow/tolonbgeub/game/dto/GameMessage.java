package team.overfullow.tolonbgeub.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import team.overfullow.tolonbgeub.game.domain.MessageType;

@Data
@AllArgsConstructor
public class GameMessage {
    private MessageType type;
    private Object payload;
}

