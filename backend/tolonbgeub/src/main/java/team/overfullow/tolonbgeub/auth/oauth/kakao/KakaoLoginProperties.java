package team.overfullow.tolonbgeub.auth.oauth.kakao;

import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoLoginProperties {
    @NotBlank
    String clientId;
    @NotBlank
    String redirectUrl;
    @NotBlank
    String clientSecret;
}
