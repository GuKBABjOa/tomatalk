package team.overfullow.tolonbgeub.debate.subject.dto;

import lombok.Builder;

public record RandomSubjectResponse(String subject1,
                            String subject2,
                            String subject3,
                            String subject4
                              ) {

    @Builder
    public RandomSubjectResponse {
    }

}
