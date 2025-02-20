package team.overfullow.tolonbgeub.debate.playing.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.debate.playing.state.PlayingStateManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class DebateObserverManager {

    private final ConcurrentMap<String, AtomicInteger> topicSubscribers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> sessionSubscriptions = new ConcurrentHashMap<>(); // sessionId -> debateId 매핑
    private final PlayingStateManager playingStateManager;

    public void subscribe(String sessionId, String debateId) {
        if (sessionSubscriptions.putIfAbsent(sessionId, debateId) == null) {
            topicSubscribers.computeIfAbsent(debateId, k -> new AtomicInteger(0)).incrementAndGet();
        }
    }

    public void unsubscribe(String sessionId, String debateId) {
        sessionSubscriptions.remove(sessionId);
        topicSubscribers.computeIfPresent(debateId, (k, v) -> v.decrementAndGet() > 0 ? v : null);
    }

    public void disconnect(String sessionId) {
        String debateId = sessionSubscriptions.remove(sessionId);
        if (debateId != null) {
            topicSubscribers.computeIfPresent(debateId, (k, v) -> v.decrementAndGet() > 0 ? v : null);
        }
    }

    public int getSubscriberCount(String debateId) {
        return topicSubscribers.getOrDefault(debateId, new AtomicInteger(0)).get();
    }

    public int getObserverCount(String debateId) {
        return getSubscriberCount(debateId);
    }
}
