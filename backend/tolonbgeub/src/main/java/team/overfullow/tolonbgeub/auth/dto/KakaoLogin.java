package team.overfullow.tolonbgeub.auth.dto;

import lombok.Builder;
import lombok.Getter;
import team.overfullow.tolonbgeub.auth.oauth.kakao.KakaoTokens;
import team.overfullow.tolonbgeub.auth.oauth.OauthUserInfo;

@Getter
@Builder
public class KakaoLogin implements Login{
    String code;
    KakaoTokens kakaoTokens;
    OauthUserInfo userInfo;
}
