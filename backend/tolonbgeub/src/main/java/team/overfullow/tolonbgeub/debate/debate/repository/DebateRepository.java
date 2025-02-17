package team.overfullow.tolonbgeub.debate.debate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.overfullow.tolonbgeub.debate.debate.domain.Debate;

public interface DebateRepository extends JpaRepository<Debate, Long>, DebateQueryRepository {
}
