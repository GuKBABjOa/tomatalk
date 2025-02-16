package team.overfullow.tolonbgeub.debate.debate;

import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.core.exception.CustomException;

public class DebateException extends CustomException {
    public DebateException(HttpStatus status) {
        super(status);
    }

    public DebateException(HttpStatus status, String message) {
        super(status, message);
    }
}
