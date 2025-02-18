package team.overfullow.tolonbgeub.webrtc;

import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenViduHandler {

    private final OpenViduConfigProps openViduConfigProps;
    private OpenVidu openVidu;

    @PostConstruct
    protected void init() {
        this.openVidu = new OpenVidu(openViduConfigProps.url, openViduConfigProps.secret);
    }

    public synchronized Connection createConnection(String sessionId) {
        Session session = openVidu.getActiveSession(sessionId);
        // todo 세션에 참여하는 대상이 맞는 지 검증
        if (session == null) {
            session = initializeSession(sessionId);
        }

        session.getSessionId();
        // participatns{ {userId : '', sessionId'' ,..}, {}

        try {
            Connection connection = session.createConnection(ConnectionProperties.fromJson(Map.of()).build());
            log.debug("connection = {}", connection.getStatus());
            return connection;
//            String token = connection.getToken();
//            return token;
        } catch (OpenViduJavaClientException e) {
            log.error(e.getMessage());
            throw new OpenviduException(HttpStatus.INTERNAL_SERVER_ERROR, "OpenViduJavaClientException occurred: " + e.getMessage());
        } catch (OpenViduHttpException e) {
            log.error(e.getMessage());
            throw new OpenviduException(HttpStatus.INTERNAL_SERVER_ERROR, "OpenViduHttpException occurred: " + e.getMessage());
        }
    }

    protected Session initializeSession(String gameId) {
        try {
            // SessionProperties 디버깅
            SessionProperties properties = SessionProperties.fromJson(Map.of("customSessionId", gameId))
//                    .recordingMode(RecordingMode.ALWAYS)
//                    .defaultRecordingProperties(new RecordingProperties.Builder()
//                            .outputMode(Recording.OutputMode.COMPOSED) // 합성된 레코딩
//                            .hasAudio(true)
//                            .hasVideo(true)
//                            .build())
                    .build();

            // 세션 생성 요청
            Session session = openVidu.createSession(properties);
            log.debug("session = {}", session);
            return session;
        } catch (OpenViduJavaClientException e) {
            log.error(e.getMessage());
            throw new OpenviduException(HttpStatus.INTERNAL_SERVER_ERROR, "OpenViduJavaClientException occurred: " + e.getMessage());
        } catch (OpenViduHttpException e) {
            log.error(e.getMessage());
            throw new OpenviduException(HttpStatus.INTERNAL_SERVER_ERROR, "OpenViduHttpException occurred: " + e.getMessage());
        }
    }
}
