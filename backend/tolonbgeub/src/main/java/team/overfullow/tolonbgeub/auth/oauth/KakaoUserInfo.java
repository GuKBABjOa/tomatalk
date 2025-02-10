package team.overfullow.tolonbgeub.auth.oauth;

public record KakaoUserInfo(
        Long oauthId,
        String email
) {

    public static KakaoUserInfo of(Long oauthId, String email) {
        return new KakaoUserInfo(
                oauthId,
                email
        );
    }
}
