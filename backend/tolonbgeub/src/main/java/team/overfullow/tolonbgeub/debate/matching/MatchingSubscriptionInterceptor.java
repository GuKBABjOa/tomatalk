package team.overfullow.tolonbgeub.debate.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.debate.Category;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingSubscriptionInterceptor implements ChannelInterceptor {
    private static final String regex = "^/sub/matching\\.(\\w+)$";
    private static final Pattern pattern = Pattern.compile(regex);
    private static final String HEARTBEAT_DESTINATION = "/pub/heartbeat";

//    private final MatchingManager matchingManager;


    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        log.debug("sub message: = {}", message);

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getDestination() == null) {
            return;
        }
        String destination = accessor.getDestination();
        log.debug("sub destination: = {}", destination);

        SimpMessageType messageType = accessor.getMessageType();
        if (!Objects.equals(messageType, SimpMessageType.SUBSCRIBE)
                && !Objects.equals(messageType, SimpMessageType.UNSUBSCRIBE)) {
            return;
        }

        log.debug("try to match destination pattern");
        Matcher matcher = pattern.matcher(destination);
        if (!matcher.matches()) {
            return;
        }

        log.debug("try to get userId from header");
//        String userId = (String) accessor.getMessageHeaders().get("X-User-Id");
//        String userId = (String) accessor.getHeader("X-User-Id");
        List<String> userIdHeaders = accessor.getNativeHeader("X-User-Id");
        log.debug("userIdHeaders = {}", userIdHeaders);
        String userId = userIdHeaders.get(0);
        log.debug("userId = {}", userId);
        if (isNull(userId)) {
            throw new IllegalArgumentException("X-User-Id is necessary");
        }
        log.debug("successfully get userId from header");
//        accessor.setUser(new UserPrincipal(userId));


        log.debug("match subscription for {}", destination);
        String categoryString = matcher.group(1);
        log.debug("sub categoryString: = {}", categoryString);
        Category category = Category.fromString(categoryString)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category: " + categoryString));
        log.debug("sub category: = {}", category);

        if (accessor.getDestination().equals(HEARTBEAT_DESTINATION)) {
            handleHeartbeat(userId);
        }

//        sessionManager.registerSession(userId, accessor.getSessionId(),categoryString);
        handleMatchingSubscription(accessor, category, userId);
    }

    private void handleMatchingSubscription(StompHeaderAccessor accessor, Category category, String userId) {
        log.debug("handleMatchingSubscription accessor : {}", accessor);
        String sessionId = accessor.getSessionId();
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            log.debug("handle sub sessionId: = {}", sessionId);
//            Authentication auth = (Authentication) accessor.getUser();
//            log.debug();("sub auth={}", auth);
//            String username = auth != null ? auth.getName() : sessionId; // todo 특정 사용자에게 메시지 전달을 위해서 username 지정
//            matchingManager.addSubscriber(category, sessionId, userId);

            log.debug("New subscriber to matching topic={}:userId={}:Session ID={})",
                    category, userId, sessionId);
        } else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
            log.debug("handle unsub sessionId: = {}", sessionId);
//            matchingManager.removeSubscriber(category, sessionId, userId);
            log.debug("Unsubscribed from matching topic {}: {}", category, sessionId);
        }
    }

    private void handleHeartbeat(String userId) {
        if (userId != null) {
//            matchingService.updateHeartbeat(userId);
        }
    }


}
