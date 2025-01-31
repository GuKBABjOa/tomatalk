package team.overfullow.tolonbgeub.auth;

public enum UserRole {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String role(){
        return role;
    }
}
