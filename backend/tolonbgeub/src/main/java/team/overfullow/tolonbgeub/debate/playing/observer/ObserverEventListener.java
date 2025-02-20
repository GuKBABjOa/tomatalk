package team.overfullow.tolonbgeub.debate.playing.observer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class ObserverEventListener {

    private final DebateObserverManager debateObserverManager;
    private static final Pattern DEBATE_ID_PATTERN = Pattern.compile("^/sub/debate/(\\d+)$"); // debateId가 숫자인 경우

    private String extractDebateId(AbstractSubProtocolEvent event) {
        StompHeaderAccessor headerAccessor = getHeaderAccessor(event);
        String debateId = headerAccessor.getDestination(); // 구독한 토픽 경로
        if (debateId == null) return null;
        Matcher matcher = DEBATE_ID_PATTERN.matcher(debateId);
        return matcher.matches() ? matcher.group(1) : null;
    }

    private static StompHeaderAccessor getHeaderAccessor(AbstractSubProtocolEvent event) {
        return StompHeaderAccessor.wrap(event.getMessage());
    }

    private static String extractSessionId(AbstractSubProtocolEvent event) {
        return getHeaderAccessor(event).getSessionId();
    }

    // 사용자가 특정 토픽을 구독할 때 실행
    @Async
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        String debateId = extractDebateId(event);
        String sessionId = extractSessionId(event);
        if (debateId != null) {
            debateObserverManager.subscribe(sessionId, debateId);
            log.info("[Subscribe] sessionId: {}, debateId: {}, Subscribers: {}", sessionId, debateId, debateObserverManager.getSubscriberCount(debateId));
        }
    }

    // 사용자가 특정 토픽을 구독 해제할 때 실행
    @Async
    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String debateId = extractDebateId(event);
        String sessionId = extractSessionId(event);
        if (debateId != null) {
            debateObserverManager.unsubscribe(sessionId, debateId);
            log.info("[Unsubscribe] sessionId: {}, debateId: {}, Subscribers: {}", sessionId, debateId, debateObserverManager.getSubscriberCount(debateId));
        }
    }

    @Async
    @EventListener
    public void handleSessionSessionDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = extractSessionId(event);
        debateObserverManager.disconnect(sessionId);
        log.info("[Disconnect] sessionId: {}", sessionId);
    }
}
