package team.overfullow.tolonbgeub.webrtc;

import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.core.exception.CustomException;

public class OpenviduException extends CustomException {
    public OpenviduException(HttpStatus status) {
        super(status);
    }

    public OpenviduException(HttpStatus status, String message) {
        super(status, message);
    }
}
