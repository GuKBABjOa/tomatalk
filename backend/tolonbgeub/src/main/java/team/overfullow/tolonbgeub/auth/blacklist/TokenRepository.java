package team.overfullow.tolonbgeub.auth.blacklist;

import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, String> {
}
