package team.overfullow.tolonbgeub.webrtc;

import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OpenviduController {

    private final OpenviduConfigProps openviduConfigProps;

    private OpenVidu openvidu;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(openviduConfigProps.openviduUrl, openviduConfigProps.openviduSecret);
    }

    /**
     * @param params The Session properties
     * @return The Session ID
     */
    @PostMapping("/api/games")
    public ResponseEntity<String> initializeSession(@RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        try {
            log.info("params = {}", params);

            // SessionProperties 디버깅
            SessionProperties properties = SessionProperties.fromJson(params)
//                    .recordingMode(RecordingMode.ALWAYS)
//                    .defaultRecordingProperties(new RecordingProperties.Builder()
//                            .outputMode(Recording.OutputMode.COMPOSED) // 합성된 레코딩
//                            .hasAudio(true)
//                            .hasVideo(true)
//                            .build())
                    .build();

            // 세션 생성 요청
            Session session = openvidu.createSession(properties);
            log.debug("session = {}", session);
            return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
        } catch (OpenViduJavaClientException e) {
            e.printStackTrace();
            return new ResponseEntity<>("OpenViduJavaClientException occurred: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (OpenViduHttpException e) {
            e.printStackTrace();
            return new ResponseEntity<>("OpenViduHttpException occurred: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @param gameId The Session in which to create the Connection
     * @param params    The Connection properties
     * @return The Token associated to the Connection
     */
    @PostMapping("/api/games/{gameId}/connections")
    public ResponseEntity<String> createConnection(@PathVariable("gameId") String gameId,
                                                   @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(gameId);
        // todo 세션에 참여하는 대상이 맞는 지 검증
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties); // openvidu에게 커넥션 생성 요청
        log.debug("connection = {}", connection.getStatus());
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }
}
