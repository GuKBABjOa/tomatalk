package team.overfullow.tolonbgeub.auth.oauth;

import org.springframework.http.HttpStatus;
import team.overfullow.tolonbgeub.core.exception.CustomException;

public class OauthException extends CustomException {
    public OauthException(HttpStatus status) {
        super(status);
    }

    public OauthException(HttpStatus status, String message) {
        super(status, message);
    }
}
