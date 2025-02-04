package team.overfullow.tolonbgeub.core.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    HttpStatus status;

    protected CustomException(HttpStatus status) {
        super("예외가 발생했습니다");
        this.status = status;
    }

    protected CustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
