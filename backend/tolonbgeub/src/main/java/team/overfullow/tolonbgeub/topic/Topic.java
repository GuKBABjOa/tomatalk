package team.overfullow.tolonbgeub.topic;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.core.auditing.BaseTimeEntity;


@Entity
@Table(name = "topics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Topic extends BaseTimeEntity {
    @Id
    private Long id;
    private String topic;
    private String description;

    @Builder
    protected Topic(Long id, String topic, String description){
        this.id = id;
        this.topic = topic;
        this.description = description;
    }
}
