package team.overfullow.tolonbgeub.auth.security;

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
import team.overfullow.tolonbgeub.auth.AuthException;

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
        log.debug("Handle 401 Error: requestURI = {} {}", request.getMethod(), request.getRequestURI());
        // CORS 헤더 추가
        response.setHeader("Access-Control-Allow-Origin", "*"); // 또는 특정 도메인 설정
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        String msg = (String) request.getAttribute("msg");
        msg = isNull(msg) ? authException.getMessage() : msg;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
//        response.getWriter().write("{\"error\": \"" + msg + "\"}");
        handlerExceptionResolver.resolveException(request, response, null, new AuthException(HttpStatus.UNAUTHORIZED, msg));
    }
}
