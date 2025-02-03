package team.overfullow.tolonbgeub.game.exception;

import lombok.Data;

@Data
public class GameException extends RuntimeException {
    private final GameErrorCode errorCode;

    public GameException(GameErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

