package team.overfullow.tolonbgeub.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.debate.Category;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingSubscriptionInterceptor implements ChannelInterceptor {
    private static final String regex = "^/sub/matching\\.(\\w+)$";
    private static final Pattern pattern = Pattern.compile(regex);
    private final MatchingService matchingService;

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        log.info("sub message: = {}",message);
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info(accessor != null ? accessor.getDestination() : null);
        if (accessor == null || accessor.getDestination() == null) {
            return;
        }

        String destination = accessor.getDestination();
        log.info("sub destination: = {}",destination);
        Matcher matcher = pattern.matcher(destination);
        if(matcher.matches()){
            log.info("match subscription for {}", destination);
            String categoryString = matcher.group(1);
            log.info("sub categoryString: = {}",categoryString);
            Category category = Category.fromString(categoryString)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category: " + categoryString));
            log.info("sub category: = {}",category);
            handleMatchingSubscription(accessor, category);
        }
    }

    private void handleMatchingSubscription(StompHeaderAccessor accessor, Category category) {
        String sessionId = accessor.getSessionId();

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Authentication auth = (Authentication) accessor.getUser();
            log.info("sub auth={}",auth);
            String username = auth != null ? auth.getName() : "anonymous";
            matchingService.addSubscriber(category, sessionId, accessor.getSubscriptionId(), username);

            log.info("New subscriber to matching topic={}:printcipal={}:Session ID={})",
                    category, username, sessionId);
        } else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
            matchingService.removeSubscriber(category, sessionId);
            log.info("Unsubscribed from matching topic {}: {}", category, sessionId);
        }
    }
}
