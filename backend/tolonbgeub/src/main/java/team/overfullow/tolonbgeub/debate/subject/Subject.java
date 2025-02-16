package team.overfullow.tolonbgeub.debate.subject;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.core.auditing.BaseTimeEntity;
import team.overfullow.tolonbgeub.debate.Category;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subject extends BaseTimeEntity {
    @Id
    private Long id;
    private Category category;
    private String subject;
    private String description;

    @Builder
    protected Subject(Long id, Category category, String subject, String description) {
        this.id = id;
        this.category = category;
        this.subject = subject;
        this.description = description;
    }
}
