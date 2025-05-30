package team.overfullow.tolonbgeub.user.Repository;

import team.overfullow.tolonbgeub.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);
}
