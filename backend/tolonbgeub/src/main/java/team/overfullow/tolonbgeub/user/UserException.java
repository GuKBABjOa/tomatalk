package team.overfullow.tolonbgeub.user;

import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.core.exception.CustomException;

public class UserException extends CustomException {
    public UserException(HttpStatus status) {
        super(status);
    }

    public UserException(HttpStatus status, String message) {
        super(status, message);
    }
}
