package team.overfullow.tolonbgeub.auth.oauth;

public record OauthUserInfo(
        String email
) {

    public static OauthUserInfo of(String email) {
        return new OauthUserInfo(
                email
        );
    }
}
