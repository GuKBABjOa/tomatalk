package team.overfullow.tolonbgeub.debate.debate.dto;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.debate.domain.DebateStatus;

import java.time.LocalDateTime;

public record DebateInfoResponse(
        String debateId,
        String category,
        String subject,                 // 논제
        DebateStatus status,            // 상태
        Integer startedAtHour,          // 시작 hour,
        Integer startedAtMinute,        // 시작 minute,
        Integer estimatedTimeMinute,    // 예상 소요 시간(분)
        int spectatorsCount,         // 관전자 수
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
){
    @Builder
    public DebateInfoResponse {
    }
}