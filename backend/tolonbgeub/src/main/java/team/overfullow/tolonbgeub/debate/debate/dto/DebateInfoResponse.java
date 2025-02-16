package team.overfullow.tolonbgeub.debate.debate.dto;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.debate.DebateStatus;

public record DebateInfoResponse(
        String debateId,
        String category,
        String subject,                 // 논제
        DebateStatus status,            // 상태
        Integer startedAtHour,          // 시작 시간,
        Integer startedAtMinute,        // 시작 시간,
        Integer estimatedTimeMinute,    // 예상 소요 시간(분)
        Integer spectatorsCount         // 관전자 수
){
    @Builder
    public DebateInfoResponse {
    }
}