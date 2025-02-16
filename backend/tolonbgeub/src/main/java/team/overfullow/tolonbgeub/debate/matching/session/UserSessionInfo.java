package team.overfullow.tolonbgeub.debate.matching.session;

import lombok.Getter;
import team.overfullow.tolonbgeub.debate.Category;

import java.time.LocalDateTime;

@Getter
public class UserSessionInfo {
    private String userId;           // 고유한 사용자 ID
    private String sessionId;        // 현재 WebSocket 세션 ID
    private int sequence;
    private Category category;         // 참여 중인 카테고리
    private LocalDateTime lastHeartbeat; // 마지막 하트비트 시간
    private SessionStatus status;    // 세션 상태


    public UserSessionInfo(String userId, String sessionId, int sequence, Category category, LocalDateTime lastHeartbeat, SessionStatus status) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.sequence = sequence;
        this.category = category;
        this.lastHeartbeat = lastHeartbeat;
        this.status = status;
    }

}