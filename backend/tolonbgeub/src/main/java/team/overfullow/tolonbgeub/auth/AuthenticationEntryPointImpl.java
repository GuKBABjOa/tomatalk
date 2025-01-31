package team.overfullow.tolonbgeub.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    public AuthenticationEntryPointImpl(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("Handle 401 Error: requestURI = {}", request.getRequestURI());
        String msg = (String) request.getAttribute("msg");
        msg = isNull(msg) ? authException.getMessage() : msg;
        handlerExceptionResolver.resolveException(request, response, null, new AuthException(HttpStatus.UNAUTHORIZED, msg));
    }
}
