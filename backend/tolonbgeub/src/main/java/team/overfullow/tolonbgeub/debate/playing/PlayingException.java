package team.overfullow.tolonbgeub.debate.playing;

import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.core.exception.CustomException;

public class PlayingException extends CustomException {
    public PlayingException(HttpStatus status) {
        super(status);
    }

    public PlayingException(HttpStatus status, String message) {
        super(status, message);
    }
}
