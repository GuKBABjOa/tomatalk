package team.overfullow.tolonbgeub.auth.oauth;

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

    public KakaoTokens getKakaoTokens(String code) {
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
        } else {
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
            throw new OauthException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    public KakaoUserInfo getUserInfo(String accessToken) {
//        String url = userInfoApi;
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + accessToken);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity,
//                String.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return parseKakaoUserInfo(response.getBody());
//        } else {
//            throw new OauthException(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    private KakaoUserInfo parseKakaoUserInfo(String response) {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        try {
//            JsonNode jsonNode = objectMapper.readTree(response);
//
//            Long oauthId = jsonNode.get("id").asLong();
//            String email = jsonNode.path("kakao_account")
//                    .path("profile")
//                    .get("email")
//                    .asText();
//
//            return KakaoUserInfo.of(oauthId, email);
//        } catch (JsonProcessingException e) {
//            throw new OauthException(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
