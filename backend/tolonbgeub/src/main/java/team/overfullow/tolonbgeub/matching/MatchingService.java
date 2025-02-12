package team.overfullow.tolonbgeub.matching;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.matching.event.SubscriberUpdateEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {
    // 카테고리별 구독자 관리: Map<Category, Map<SessionId, UserInfo>>
    private final Map<Category, Map<String, UserInfo>> categorySubscribers = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;


    public void addSubscriber(Category category, String sessionId, String subscriptionId, String username) {
        categorySubscribers.computeIfAbsent(category, k -> new ConcurrentHashMap<>())
                .put(sessionId, new UserInfo(username, subscriptionId));
        broadcastSubscriberUpdate(category);
    }

    public void removeSubscriber(Category category, String sessionId) {
        Map<String, UserInfo> subscribers = categorySubscribers.get(category);
        if (subscribers != null) {
            subscribers.remove(sessionId);
            if (subscribers.isEmpty()) {
                categorySubscribers.remove(category);
            }
            broadcastSubscriberUpdate(category);
        }
    }

    public void removeSubscriberFromAllCategories(String sessionId) {
        categorySubscribers.entrySet().removeIf(entry -> {
            Map<String, UserInfo> subscribers = entry.getValue();
            if (subscribers.remove(sessionId) != null) {
                broadcastSubscriberUpdate(entry.getKey());
                return subscribers.isEmpty();
            }
            return false;
        });
    }

    public int getSubscriberCount(Category category) {
        return categorySubscribers.getOrDefault(category, Collections.emptyMap()).size();
    }

    public List<String> getSubscribers(Category category) {
        return categorySubscribers.getOrDefault(category, Collections.emptyMap())
                .values()
                .stream()
                .map(UserInfo::getUsername)
                .collect(Collectors.toList());
    }

    private void broadcastSubscriberUpdate(Category category) {
        SubscriberUpdateDTO update = new SubscriberUpdateDTO(
                category,
                getSubscriberCount(category),
                getSubscribers(category)
        );
        log.info("broadcasting subscriber update: {}", update);
        eventPublisher.publishEvent(new SubscriberUpdateEvent(
                "/sub/matching." + category,
                update
        ));
    }

    @Data
    @AllArgsConstructor
    class UserInfo {
        private String username;
        private String subscriptionId;
    }

    @Data
    @AllArgsConstructor
    public class SubscriberUpdateDTO {
        private Category category;
        private int subscriberCount;
        private List<String> subscribers;
    }

}
