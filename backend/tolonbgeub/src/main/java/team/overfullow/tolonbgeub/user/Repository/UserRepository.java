package team.overfullow.tolonbgeub.user.Repository;

import team.overfullow.tolonbgeub.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Integer countByNickname(String nickname);
}
