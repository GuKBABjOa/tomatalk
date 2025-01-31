package team.overfullow.tolonbgeub.auth;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public abstract class CustomAuthenticationToken extends AbstractAuthenticationToken {

    public CustomAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public abstract UserId getPrincipal();
}