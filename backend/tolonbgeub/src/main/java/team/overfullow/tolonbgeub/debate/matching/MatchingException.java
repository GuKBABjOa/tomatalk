package team.overfullow.tolonbgeub.debate.matching;

import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.core.exception.CustomException;

public class MatchingException extends CustomException {
    public MatchingException(HttpStatus status) {
        super(status);
    }

    public MatchingException(HttpStatus status, String message) {
        super(status, message);
    }
}
