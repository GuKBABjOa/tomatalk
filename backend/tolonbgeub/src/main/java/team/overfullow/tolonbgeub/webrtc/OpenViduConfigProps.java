package team.overfullow.tolonbgeub.webrtc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenViduConfigProps {
    @Getter
    @Value("${OPENVIDU_URL}")
    String url;

    @Value("${OPENVIDU_SECRET}")
    String secret;
}
