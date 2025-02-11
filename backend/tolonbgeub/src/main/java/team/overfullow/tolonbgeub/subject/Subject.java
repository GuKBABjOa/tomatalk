package team.overfullow.tolonbgeub.subject;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.core.auditing.BaseTimeEntity;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subject extends BaseTimeEntity {
    @Id
    private Long id;
    private String subject;
    private String description;

    @Builder
    protected Subject(Long id, String subject, String description) {
        this.id = id;
        this.subject = subject;
        this.description = description;
    }
}
