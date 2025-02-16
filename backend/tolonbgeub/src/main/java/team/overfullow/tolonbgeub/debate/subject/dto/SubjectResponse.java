package team.overfullow.tolonbgeub.debate.subject.dto;

import lombok.Builder;

public record SubjectResponse(String subjectId,
                              String subject,
                              String description) {

    @Builder
    public SubjectResponse {
    }

}
