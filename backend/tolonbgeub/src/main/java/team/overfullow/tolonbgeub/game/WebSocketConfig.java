package team.overfullow.tolonbgeub.game;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import team.overfullow.tolonbgeub.matching.MatchingSubscriptionInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final MatchingSubscriptionInterceptor matchingSubscriptionInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
//                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub", "/user") // 메시지를 브로드캐스트할 프리픽스
                .setTaskScheduler(taskScheduler());// 스케줄러 설정
        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트에서 보낼 메시지 프리픽스
        registry.setUserDestinationPrefix("/user");
        registry.setCacheLimit(1024 * 1024);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(matchingSubscriptionInterceptor);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        // 기본 스케줄러를 설정
        return new ConcurrentTaskScheduler();
    }
}