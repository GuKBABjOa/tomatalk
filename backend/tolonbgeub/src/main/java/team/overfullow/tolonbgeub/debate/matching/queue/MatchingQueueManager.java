package team.overfullow.tolonbgeub.debate.matching.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.debate.Category;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class MatchingQueueManager {
    private final Map<Category, MatchingQueue> matchingQueues = new ConcurrentHashMap<>();

    public MatchingQueue getByCategory(Category category) {
        return matchingQueues.computeIfAbsent(category, MatchingQueue::new);
    }
}
