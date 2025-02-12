package team.overfullow.tolonbgeub.auth.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import team.overfullow.tolonbgeub.auth.AuthConfigProps;
import team.overfullow.tolonbgeub.auth.UserRole;
import team.overfullow.tolonbgeub.auth.jwt.JwtProvider;
import team.overfullow.tolonbgeub.auth.jwt.JwtType;
import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.dto.UserResponse;
import team.overfullow.tolonbgeub.user.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.substringAfter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthConfigProps authConfigProps;
    private final AuthenticationManager authenticationManager;
    private final UserService userService; // for test
    private final JwtProvider jwtProvider; // for test

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //todo log 수정
        log.info("JwtAuthenticationFilter: in progress: request = {} {} {}", request.getMethod(), request.getRequestURI(), request.getQueryString());
        String authHeader = request.getHeader(authConfigProps.header);

        log.info("JwtAuthenticationFilter: authHeader = {}", authHeader);
        if (authHeader == null || !authHeader.startsWith(authConfigProps.scheme)) {
            log.info("authProps.scheme = {}", authConfigProps.scheme);
            request.setAttribute("msg", "인증 헤더가 올바르지 않습니다 header = "+authHeader);
            filterChain.doFilter(request, response);
            return;
        }

        var accessToken = substringAfter(authHeader, authConfigProps.scheme).trim();
        if(accessToken.equals("test")) {
            log.info("테스트 유저 인증");
            Optional<UserResponse> byEmail =
                    userService.findByEmail("test@test.com");
            if(byEmail.isPresent()) {
                accessToken = jwtProvider.generate(byEmail.get().userId().toString(), List.of(UserRole.USER.role()), JwtType.ACCESS).value();
            }else {
                Long userId = userService.createUser(UserRequest.builder()
                        .email("test@test.com")
                        .nickname("test")
                        .build()).userId();
                accessToken = jwtProvider.generate(userId.toString(), List.of(UserRole.USER.role()), JwtType.ACCESS).value();
            }

        }
        var unauthenticated = JwtAuthenticationToken.unauthenticated(accessToken);
        var authentication = authenticationManager.authenticate(unauthenticated);

        if(!authentication.isAuthenticated()){
            request.setAttribute("msg", "인증 토큰이 유효하지 않습니다");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("인증 성공 !! AuthUser = {}",authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
