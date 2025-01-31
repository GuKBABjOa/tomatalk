package team.overfullow.tolonbgeub.auth;

import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.core.exception.CustomException;

public class AuthException extends CustomException {
    public AuthException(HttpStatus status) {
        super(status);
    }

    public AuthException(HttpStatus status, String message) {
        super(status, message);
    }
}
