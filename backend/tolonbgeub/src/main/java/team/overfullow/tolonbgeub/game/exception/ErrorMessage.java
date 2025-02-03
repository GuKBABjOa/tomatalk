package team.overfullow.tolonbgeub.game.exception;

import lombok.Data;

// 에러 메시지 클래스
@Data
public class ErrorMessage {
    private GameErrorCode code;
    private String message;
    private long timestamp;

    public ErrorMessage(GameErrorCode code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}
