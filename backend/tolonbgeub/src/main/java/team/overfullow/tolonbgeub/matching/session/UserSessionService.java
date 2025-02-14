package team.overfullow.tolonbgeub.matching.session;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionService {

    // userId -> sessionId 매핑 (유저 상태 유지)
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();
    private final Map<String, Long> lastHeartbeatMap = new ConcurrentHashMap<>();

    private static final long HEARTBEAT_TIMEOUT = 10_000; // 10초 이상 heartbeat 없으면 세션 종료

    public void registerUserSession(String userId, String sessionId) {
        userSessionMap.put(userId, sessionId);
        lastHeartbeatMap.put(userId, System.currentTimeMillis());
    }

    public void updateHeartbeat(String userId) {
        lastHeartbeatMap.put(userId, System.currentTimeMillis());
    }

    public boolean isSessionActive(String userId) {
        Long lastHeartbeat = lastHeartbeatMap.get(userId);
        return lastHeartbeat != null && (System.currentTimeMillis() - lastHeartbeat) < HEARTBEAT_TIMEOUT;
    }

    public void removeUserSession(String userId) {
        userSessionMap.remove(userId);
        lastHeartbeatMap.remove(userId);
    }
}

