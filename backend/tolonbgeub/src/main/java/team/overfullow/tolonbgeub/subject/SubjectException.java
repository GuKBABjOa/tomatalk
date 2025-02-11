package team.overfullow.tolonbgeub.subject;

import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.core.exception.CustomException;

public class SubjectException extends CustomException {
    public SubjectException(HttpStatus status) {
        super(status);
    }

    public SubjectException(HttpStatus status, String message) {
        super(status, message);
    }
}
