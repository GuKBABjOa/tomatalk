package team.overfullow.tolonbgeub.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import team.overfullow.tolonbgeub.auth.AuthConfigProps;
import team.overfullow.tolonbgeub.auth.UserRole;
import team.overfullow.tolonbgeub.auth.jwt.JwtProvider;
import team.overfullow.tolonbgeub.auth.oauth.kakao.KakaoAuthenticationProvider;
import team.overfullow.tolonbgeub.user.service.UserService;

import java.util.List;


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
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(GET, "/api/auth/login/**").permitAll()
                        .requestMatchers(POST, "/api/auth/login/**").permitAll()
                        .requestMatchers(POST, "/api/auth/logout").authenticated()
                        .requestMatchers(GET, "/api/auth/authentication").authenticated()
                        .requestMatchers(GET, "/api/auth/authorization").hasAuthority(UserRole.ADMIN.role())
                        .requestMatchers(GET, "/api/topics/**").permitAll()
                        .requestMatchers(GET, "/api/games/sample/**").permitAll()
                        .requestMatchers(GET, "/api/users/**").permitAll()
                        .requestMatchers(PUT, "/api/users/me").permitAll()
                        .requestMatchers(PUT, "/api/users/change").authenticated()
                        .requestMatchers(GET, "/api/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authenticationEntryPointImpl)
                        .accessDeniedHandler(accessDeniedHandlerImpl))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ✅ CORS 설정 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Vue 요청 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 허용 (필요한 경우)

        source.registerCorsConfiguration("/**", configuration);
        return (CorsConfigurationSource) source; // 명시적으로 CorsConfigurationSource로 반환
    }



    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(kakaoAuthenticationProvider, jwtAuthenticationProvider);
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authConfigProps, authenticationManager(), userService, jwtProvider);
    }
}
