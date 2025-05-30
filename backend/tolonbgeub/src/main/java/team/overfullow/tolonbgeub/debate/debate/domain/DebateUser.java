package team.overfullow.tolonbgeub.debate.debate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DebateUser {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debate_id")
    private Debate debate;

    private String position;

    @Min(1)
    private Integer positionOrder;

    @Min(1)
    private Integer speechOrder;

    @Builder
    private DebateUser(Long id, User user, Debate debate, String position, Integer positionOrder, Integer speechOrder) {
        this.id = id;
        this.user = user;
        this.debate = debate;
        this.position = position;
        this.positionOrder = positionOrder;
        this.speechOrder = speechOrder;
    }

    public void setParent(Debate debate) {
        this.debate = debate;
    }
}
