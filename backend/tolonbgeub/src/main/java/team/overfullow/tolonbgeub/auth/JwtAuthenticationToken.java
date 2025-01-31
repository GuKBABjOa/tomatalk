package team.overfullow.tolonbgeub.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;
import team.overfullow.tolonbgeub.auth.jwt.JwtClaims;

import java.util.Collection;

public class JwtAuthenticationToken extends CustomAuthenticationToken {
    private final UserId principal;
    private final Jwt credentials;
    private final JwtClaims details;

    public static JwtAuthenticationToken unauthenticated(String jwt) {
        return new JwtAuthenticationToken(jwt);
    }

    public static JwtAuthenticationToken authenticated(UserId principal, String jwt, JwtClaims details, Collection<? extends GrantedAuthority> authorities) {
        return new JwtAuthenticationToken(principal, jwt, details, authorities);
    }

    private JwtAuthenticationToken(String jwt) {
        super(null);
        this.principal = null;
        this.credentials = new Jwt(jwt);
        this.details = null;
        setAuthenticated(false);
    }

    private JwtAuthenticationToken(UserId principal, String jwt, JwtClaims details, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = new Jwt(jwt);
        this.details = details;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public UserId getPrincipal() {
        return this.principal;
    }

    @Override
    public Jwt getCredentials() {
        return this.credentials;
    }

    @Override
    public JwtClaims getDetails() {
        return this.details;
    }

    @Override
    @Deprecated
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this accessToken to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public record Jwt(String jwt) {
    }
}
