package team.overfullow.tolonbgeub.auth.blacklist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenBlackList {
    private final TokenRepository tokenRepository; // todo redis 전환

    @Transactional
    public void add(String tokenId, Instant expiresAt) {
        tokenRepository.save(new Token(tokenId, expiresAt));
    }

    public boolean exists(String tokenId) {
        return tokenRepository.findById(tokenId).isPresent();
    }
}
