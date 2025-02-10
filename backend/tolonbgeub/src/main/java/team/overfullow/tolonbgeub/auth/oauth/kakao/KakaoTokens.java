package team.overfullow.tolonbgeub.auth.oauth.kakao;

public record KakaoTokens(
        String accessToken,
        String refreshToken
) {

    public static KakaoTokens of(String accessToken, String refreshToken) {
        return new KakaoTokens(accessToken, refreshToken);
    }
}
