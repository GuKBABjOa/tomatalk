package team.overfullow.tolonbgeub.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.matching.event.MatchingSuccessEvent;
import team.overfullow.tolonbgeub.matching.event.SubscriberUpdateEvent;
import team.overfullow.tolonbgeub.matching.message.SubscriberUpdateMessage;
import team.overfullow.tolonbgeub.matching.session.SessionStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import team.overfullow.tolonbgeub.matching.session.UserSessionInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {
    // 카테고리별 구독자 관리: Map<Category, Map<UserId, UserSessionState>>
    private final Map<Category, Map<String, UserSessionInfo>> categorySubscribers = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicInteger sequenceCounter = new AtomicInteger(1);
    private final ExecutorService executorService = Executors.newFixedThreadPool(32);


    public void addSubscriber(Category category, String sessionId, String userId) {
        log.info("Adding subscriber for category {} and session id {}", category, sessionId);
        categorySubscribers.computeIfAbsent(category, k -> new ConcurrentHashMap<>())
                .put(userId, new UserSessionInfo(
                        userId,
                        sessionId,
                        sequenceCounter.getAndIncrement(),
                        category,
                        LocalDateTime.now(),
                        SessionStatus.ACTIVE));
        sequenceCounter.compareAndSet(Integer.MAX_VALUE, 0);
        Map<String, UserSessionInfo> waitingQueue = categorySubscribers.getOrDefault(category, Collections.emptyMap());
        // 매칭 시도
        synchronized (waitingQueue) {
            while (waitingQueue.size() >= 4){
                List<UserSessionInfo> selectedUsers = getUsersFromQueue(waitingQueue, 4);
                executorService.execute(() -> sendMatchingSuccessEvent(category, selectedUsers));
            }
        }

        broadcastSubscriberUpdate(category);
    }

    public synchronized List<UserSessionInfo> getUsersFromQueue(Map<String, UserSessionInfo> waitingQueue, int maxCount) {
        // 정렬 후 최대 4명만 추출
        List<UserSessionInfo> selectedUsers = waitingQueue.values().stream()
                .sorted(Comparator.comparingInt(UserSessionInfo::getSequence))
                .limit(maxCount)
                .collect(Collectors.toList());

        // 추출된 4명을 대기열에서 제거
        selectedUsers.forEach(user -> waitingQueue.values().remove(user));

        return selectedUsers;
    }

    public void removeSubscriber(Category category, String sessionId, String userId) {
        log.info("Removing subscriber for category {} and session id {}", category, sessionId);
        Map<String, UserSessionInfo> subscribers = categorySubscribers.get(category);
        if (subscribers != null) {
            subscribers.remove(userId);
            if (subscribers.isEmpty()) {
                categorySubscribers.remove(category);
            }
            broadcastSubscriberUpdate(category);
        }
    }

    public void removeSubscriberFromAllCategories(String sessionId) {
        categorySubscribers.entrySet().removeIf(entry -> {
            Map<String, UserSessionInfo> subscribers = entry.getValue();
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
                .map(UserSessionInfo::getUserId)
                .collect(Collectors.toList());
    }

    private void broadcastSubscriberUpdate(Category category) {
        SubscriberUpdateMessage message = new SubscriberUpdateMessage(
                category,
                getSubscriberCount(category),
                getSubscribers(category)
        );
        log.info("broadcasting subscriber update: {}", message);
        eventPublisher.publishEvent(new SubscriberUpdateEvent(
                "/sub/matching." + category,
                message
        ));
    }

    private synchronized void sendMatchingSuccessEvent(Category category, List<UserSessionInfo> users) {
        // 토론 방 정보 생성
        // 토론 방 정보 전달

        for (UserSessionInfo user : users) {
            SubscriberUpdateMessage message = new SubscriberUpdateMessage(
                    category,
                    getSubscriberCount(category),
                    List.of("생성된 토론 방 정보 추가 예정: user = "+users.stream().map(UserSessionInfo::getUserId).toList())
            );
            eventPublisher.publishEvent(new MatchingSuccessEvent(
                    user.getUserId(),
                    "/matching."+category,
                    message
            ));
        }
    }
}
