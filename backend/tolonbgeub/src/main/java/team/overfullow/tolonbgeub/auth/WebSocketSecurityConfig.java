package team.overfullow.tolonbgeub.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSecurity
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
//                .simpDestMatchers("/app/**").authenticated()  // 애플리케이션 대상 메시지 인증 필요
//                .simpSubscribeDestMatchers("/topic/**").authenticated()  // 구독 인증 필요
//                .simpDestMatchers("/ws-game/**").permitAll()  // WebSocket 연결 엔드포인트는 허용
                .anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // CORS 설정을 위해 동일 출처 정책 비활성화
        return true;
    }
}