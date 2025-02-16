package team.overfullow.tolonbgeub.debate.session;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import team.overfullow.tolonbgeub.debate.Category;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArraySet;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
// todo 세션 유지 로직 고도하
public class ResilientSessionManager {
//    private final Map<String, UserSessionState> userSessions = new ConcurrentHashMap<>();
//    private final Map<String, Set<String>> categoryUsers = new ConcurrentHashMap<>();
////    private final SimpMessagingTemplate messagingTemplate;
//
//    @Value("${websocket.temporary-disconnect-timeout:30}")  // 설정값, 기본 30초
//    private int temporaryDisconnectTimeout;
//
//    // 새로운 세션 등록
//    public void registerSession(String userId, String sessionId, String category) {
//        UserSessionState state = new UserSessionState(
//                userId,
//                sessionId,
//                Category.fromString(category),
//                LocalDateTime.now(),
//                SessionStatus.ACTIVE
//        );
//
//        userSessions.put(userId, state);
//        categoryUsers.computeIfAbsent(category, k -> new CopyOnWriteArraySet<>()).add(userId);
//
//        broadcastCategoryUpdate(category);
//    }
//
//    // 하트비트 업데이트
//    public void updateHeartbeat(String userId) {
//        UserSessionState state = userSessions.get(userId);
//        if (state != null) {
//            state.setLastHeartbeat(LocalDateTime.now());
//            if (state.getStatus() == UserSessionState.SessionStatus.TEMPORARILY_DISCONNECTED) {
//                state.setStatus(UserSessionState.SessionStatus.ACTIVE);
//                broadcastCategoryUpdate(state.getCategory());
//            }
//        }
//    }
//
//    // 세션 연결 해제 처리
//    public void handleDisconnect(String sessionId) {
//        userSessions.values().stream()
//                .filter(state -> state.getSessionId().equals(sessionId))
//                .findFirst()
//                .ifPresent(state -> {
//                    state.setStatus(UserSessionState.SessionStatus.TEMPORARILY_DISCONNECTED);
//                    // 상태 변경만 하고 즉시 제거하지 않음
//                });
//    }
//
//    // 정기적으로 세션 상태 검사 (스케줄러)
//    @Scheduled(fixedRate = 10000)  // 10초마다 실행
//    public void checkSessions() {
//        LocalDateTime now = LocalDateTime.now();
//        userSessions.forEach((userId, state) -> {
//            if (state.getStatus() == UserSessionState.SessionStatus.TEMPORARILY_DISCONNECTED) {
//                Duration disconnectedDuration = Duration.between(state.getLastHeartbeat(), now);
//                if (disconnectedDuration.getSeconds() > temporaryDisconnectTimeout) {
//                    // 타임아웃 초과시 영구 삭제
//                    removeUser(userId);
//                }
//            }
//        });
//    }
//
//    // 사용자 완전 제거
//    private void removeUser(String userId) {
//        UserSessionState state = userSessions.remove(userId);
//        if (state != null) {
//            categoryUsers.get(state.getCategory()).remove(userId);
//            broadcastCategoryUpdate(state.getCategory());
//        }
//    }
//
//    // 카테고리 상태 브로드캐스트
//    private void broadcastCategoryUpdate(String category) {
//        Set<String> users = categoryUsers.get(category);
//        if (users != null) {
//            CategoryUpdateDto update = new CategoryUpdateDto(
//                    category,
//                    users.size(),
//                    users.stream()
//                            .map(userId -> new UserStatus(userId,
//                                    userSessions.get(userId).getStatus()))
//                            .collect(Collectors.toList())
//            );
//            //todo event
////            messagingTemplate.convertAndSend("/sub/matching/" + category, update);
//        }
//    }
}