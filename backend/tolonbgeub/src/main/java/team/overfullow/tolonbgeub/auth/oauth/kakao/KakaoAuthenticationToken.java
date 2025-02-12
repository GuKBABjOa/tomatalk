package team.overfullow.tolonbgeub.auth.oauth.kakao;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;
import team.overfullow.tolonbgeub.auth.UserId;
import team.overfullow.tolonbgeub.auth.security.CustomAuthentication;

import java.util.Collection;

public class KakaoAuthenticationToken extends AbstractAuthenticationToken implements CustomAuthentication {
    private final UserId principal;
    private final Code credentials;

    public static KakaoAuthenticationToken unauthenticated(String code) {
        return new KakaoAuthenticationToken(code);
    }

    public static KakaoAuthenticationToken authenticated(UserId principal, Collection<? extends GrantedAuthority> authorities) {
        return new KakaoAuthenticationToken(principal, authorities);
    }

    private KakaoAuthenticationToken(UserId principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;
        super.setAuthenticated(true); // must use super, as we override
    }

    private KakaoAuthenticationToken(String code) {
        super(null);
        this.principal = null;
        this.credentials = new Code(code);
        setAuthenticated(false);
    }

    @Override
    public UserId getPrincipal() {
        return this.principal;
    }


    @Override
    public Code getCredentials() {
        return this.credentials;
    }

    @Override
    @Deprecated
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this accessToken to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public record Code(String code){
    }
}
