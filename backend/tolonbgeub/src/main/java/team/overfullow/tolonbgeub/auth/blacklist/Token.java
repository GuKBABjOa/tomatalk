package team.overfullow.tolonbgeub.auth.blacklist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.core.auditing.CreateTimeEntity;

import java.time.Instant;


@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token extends CreateTimeEntity {
    @Id
    private String id;
    @Column(nullable = false)
    private Instant expiresAt;

    public Token(String id, Instant expiresAt) {
        this.id = id;
        this.expiresAt = expiresAt;
    }
}
