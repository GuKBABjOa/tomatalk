package team.overfullow.tolonbgeub.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.overfullow.tolonbgeub.auth.AuthConfigProps;
import team.overfullow.tolonbgeub.auth.UserRole;
import team.overfullow.tolonbgeub.auth.jwt.JwtProvider;
import team.overfullow.tolonbgeub.auth.oauth.kakao.KakaoAuthenticationProvider;
import team.overfullow.tolonbgeub.auth.security.AccessDeniedHandlerImpl;
import team.overfullow.tolonbgeub.auth.security.AuthenticationEntryPointImpl;
import team.overfullow.tolonbgeub.auth.security.JwtAuthenticationFilter;
import team.overfullow.tolonbgeub.auth.security.JwtAuthenticationProvider;
import team.overfullow.tolonbgeub.user.service.UserService;


import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthConfigProps authConfigProps;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final KakaoAuthenticationProvider kakaoAuthenticationProvider;
    private final AuthenticationEntryPointImpl authenticationEntryPointImpl;
    private final AccessDeniedHandlerImpl accessDeniedHandlerImpl;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(POST, "/api/auth/logout").authenticated()
                        .requestMatchers(GET, "/api/auth/authentication").authenticated()
                        .requestMatchers(GET, "/api/auth/authorization").hasAuthority(UserRole.ADMIN.role())
                        .requestMatchers(GET, "/api/users/me").authenticated()
                        .requestMatchers(PUT, "/api/users/change").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authenticationEntryPointImpl)
                        .accessDeniedHandler(accessDeniedHandlerImpl))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(), // 정적 리소스 (css, js 등) 허용
                        PathRequest.toH2Console() // H2 콘솔 허용 (필요 시)
                );
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(kakaoAuthenticationProvider, jwtAuthenticationProvider);
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authConfigProps, authenticationManager(), userService, jwtProvider);
    }
}
