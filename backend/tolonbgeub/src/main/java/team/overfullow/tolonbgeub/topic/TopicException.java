package team.overfullow.tolonbgeub.topic;

import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.core.exception.CustomException;

public class TopicException extends CustomException {
    public TopicException(HttpStatus status) {
        super(status);
    }

    public TopicException(HttpStatus status, String message) {
        super(status, message);
    }
}
