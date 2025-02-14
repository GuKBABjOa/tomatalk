package team.overfullow.tolonbgeub.debate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.core.auditing.BaseTimeEntity;
import team.overfullow.tolonbgeub.subject.Subject;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Debate extends BaseTimeEntity {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @OneToMany(mappedBy = "debate", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DebateUser> debateUsers = new ArrayList<>();

    @Builder
    private Debate(Long id, Category category, Subject subject, List<DebateUser> debateUsers) {
        this.id = id;
        this.category = category;
        this.subject = subject;
        this.debateUsers = debateUsers;
        debateUsers.stream().forEach(du->du.setParent(this));
    }
}
