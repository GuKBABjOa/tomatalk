package team.overfullow.tolonbgeub.auth.oauth.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.overfullow.tolonbgeub.auth.oauth.OauthException;
import team.overfullow.tolonbgeub.auth.oauth.OauthUserInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${oauth.kakao.access_token_api}")
    private String accessTokenApi;

    @Value("${oauth.kakao.get_userinfo_api}")
    private String userInfoApi;

    private final KakaoLoginProperties kakaoLoginProperties;
    private final RestTemplate restTemplate;

    public String getLoginUrl() {
        return String.format("https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                kakaoLoginProperties.clientId,
                kakaoLoginProperties.redirectUrl);
    }

    public OauthUserInfo login(String code) {
        KakaoTokens kakaoTokens = getKakaoTokens(code);
        return getUserInfo(kakaoTokens.accessToken());
    }

    private KakaoTokens getKakaoTokens(String code) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(accessTokenApi)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", kakaoLoginProperties.clientId)
                .queryParam("code", code)
                .queryParam("client_secret", kakaoLoginProperties.clientSecret)
                .queryParam("redirect_uri", kakaoLoginProperties.redirectUrl);


        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url =uriBuilder.toUriString();
        log.info("url: " + url);
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, String.class);

        log.info("response: {}", response);
        if (response.getStatusCode() == HttpStatus.OK) {
            return parseKakaoTokens(response.getBody());
        } else if(response.getStatusCode().is4xxClientError()){
            log.error("카카오 로그인 클라이언트 오류: {}", response.getBody());
            throw new OauthException(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 Code 입니다.");
        }else {
            throw new OauthException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 로그인 실패");
        }
    }

    private KakaoTokens parseKakaoTokens(String response) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(response);

            String accessToken = jsonNode.get("access_token").asText();
            String refreshToken = jsonNode.get("refresh_token").asText();

            return KakaoTokens.of(accessToken, refreshToken);
        } catch (JsonProcessingException e) {
            throw new OauthException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 로그인 실패");
        }
    }

    private OauthUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        log.info("userInfoApi: {}", userInfoApi);

        ResponseEntity<String> response = restTemplate.exchange(userInfoApi, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return parseKakaoUserInfo(response.getBody());
        } else {
            throw new OauthException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 로그인 실패");
        }
    }

    private OauthUserInfo parseKakaoUserInfo(String response) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(response);

            String email = jsonNode.path("kakao_account")
                    .path("email")
                    .asText();

            return OauthUserInfo.of(email);
        } catch (JsonProcessingException e) {
            throw new OauthException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
