package team.overfullow.tolonbgeub.auth.security;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import team.overfullow.tolonbgeub.auth.UserId;

import java.util.Collection;

public abstract class CustomAuthenticationToken extends AbstractAuthenticationToken {

    public CustomAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public abstract UserId getPrincipal();
}