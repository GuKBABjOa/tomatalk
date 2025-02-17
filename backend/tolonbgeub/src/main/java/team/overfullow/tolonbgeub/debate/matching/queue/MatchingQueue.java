package team.overfullow.tolonbgeub.debate.matching.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.overfullow.tolonbgeub.debate.Category;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MatchingQueue {
    private static final int MATCHING_SIZE = 4;
    private final Map<Long, WaitingUser> map = new ConcurrentHashMap<>();
    private final AtomicInteger registerSequence = new AtomicInteger(0);
    @Getter
    private final Category category;
    private final ReentrantLock matchLock = new ReentrantLock();

    public MatchingQueue(Category category) {
        this.category = category;
    }

    public void register(Long userId) {
        matchLock.lock();
        try {
            if (map.containsKey(userId)) {
                throw new IllegalStateException("이미 대기열에 등록된 사용자입니다.");
            }

            registerSequence.compareAndSet(Integer.MAX_VALUE, 0);
            map.put(userId, new WaitingUser(userId, registerSequence.getAndIncrement()));
        } finally {
            matchLock.unlock();
        }
    }

    public void remove(Long userId) {
        matchLock.lock();
        try {
            WaitingUser removedUser = map.remove(userId);
            if (removedUser == null) {
                throw new IllegalStateException("대기열에 존재하지 않는 사용자입니다.");
            }
        } finally {
            matchLock.unlock();
        }
    }

    public Optional<MatchingSuccessResult> match() {
        matchLock.lock();
        try {
            if (map.size() >= MATCHING_SIZE) {
                List<Long> matchedUsers = map.values().stream()
                        .sorted(Comparator.comparingInt(WaitingUser::getSequence))
                        .limit(MATCHING_SIZE)
                        .map(WaitingUser::getUserId)
                        .collect(Collectors.toList());

                matchedUsers.forEach(map::remove);
                return Optional.of(new MatchingSuccessResult(category, matchedUsers));
            }
            return Optional.empty();
        } finally {
            matchLock.unlock();
        }
    }

    public int getWaitingUserCount() {
        matchLock.lock();
        try {
            return map.size();
        } finally {
            matchLock.unlock();
        }
    }

    public List<Long> getWaitingUserIds() {
        matchLock.lock();
        try {
            return map.values().stream().map(wu->wu.userId).toList();
        } finally {
            matchLock.unlock();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class WaitingUser {
        private Long userId;
        private int sequence;
    }
}