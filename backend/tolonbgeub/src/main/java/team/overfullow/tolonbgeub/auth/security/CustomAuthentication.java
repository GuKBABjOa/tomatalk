package team.overfullow.tolonbgeub.auth.security;


import team.overfullow.tolonbgeub.auth.UserId;

public interface CustomAuthentication {

    UserId getPrincipal();
}