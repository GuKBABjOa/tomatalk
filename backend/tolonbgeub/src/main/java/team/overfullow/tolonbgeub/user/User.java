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
    private String email;
    private String nickname;

    @Lob
    @Column(columnDefinition = "BLOB") // MySQL 기준 BLOB 컬럼 설정
    private byte[] profileImage; // 이미지 데이터 저장 (BLOB)

    @Builder
    protected User(Long id, String email, String nickname, byte[] profileImage) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }
}
