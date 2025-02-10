package team.overfullow.tolonbgeub.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.core.auditing.BaseTimeEntity;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    private Long id;
    private String nickname;

    @Builder
    protected User(Long id, String nickname){
        this.id = id;
        this.nickname = nickname;
    }
}
