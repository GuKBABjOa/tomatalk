package team.overfullow.tolonbgeub.debate.dto;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.DebateStatus;

public record DebateResponse (
        String debateId,
        String subject,             // 논제
        DebateStatus status,        // 상태
        Integer startedAtHour,      // 시작 시간,
        Integer startedAtMinute,    // 시작 시간,
        Integer estimatedTimeMinute // 예상 소요 시간(분)
){
    @Builder
    public DebateResponse{
    }
}
