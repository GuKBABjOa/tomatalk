package team.overfullow.tolonbgeub.auth.jwt;

public enum JwtClaimName {
    TOKEN_ID("tokenId"),
    USER_ID("userId"),
    ROLES("roles"),
    TYPE("type"),
    SALT("salt");

    private final String claim;

    JwtClaimName(String claim) {
        this.claim = claim;
    }

    String claim() {
        return claim;
    }
}