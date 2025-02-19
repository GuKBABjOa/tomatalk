package team.overfullow.tolonbgeub.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.overfullow.tolonbgeub.core.util.PreciseInstantScheduler;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateRoomResponse;
import team.overfullow.tolonbgeub.debate.debate.service.DebateService;
import team.overfullow.tolonbgeub.debate.matching.MatchingException;
import team.overfullow.tolonbgeub.debate.matching.MatchingService;
import team.overfullow.tolonbgeub.debate.playing.PlayingService;
import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.dto.UserResponse;
import team.overfullow.tolonbgeub.user.service.UserService;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;
    private final MatchingService matchingService;
    private final PlayingService playingService;
    private final DebateService debateService;
    private final PreciseInstantScheduler scheduler;
    /**
     * 추가적으로 3명의 유저가 1초 간격으로 {category} 매칭 대기열에 등록하는 시나리오를 수행한다.
     */
    @GetMapping("/api/tests/matching/{category}")
    public ResponseEntity<String> matchingScenario(@PathVariable(value = "category") String categoryString) {
        log.debug("test matching.join category: {}", categoryString);
        Category category = Category.fromString(categoryString)
                .orElseThrow(() -> new MatchingException(HttpStatus.NOT_FOUND, "존재하지 않는 논제 카테고리입니다."));

        for (int i = 0; i < 3; i++) {
            String username = UUID.randomUUID().toString();
            UserResponse user = userService.createUser(UserRequest.builder()
                    .email(String.format("%s@test.email", username))
                    .nickname(username)
                    .build());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            matchingService.handleJoin(category, Long.valueOf(user.userId()));
        }
        return ResponseEntity.ok("매칭 시나리오가 수행되었습니다.");
    }

    /**
     * 요청을 신청한 유저를 제외한 토론에 참여하는 나머지 3명의 유저가 {debateId} 1초 간격으로 토론에 참여하는 시나리오를 수행한다.
     */
    @GetMapping("/api/tests/debate/{debateId}")
    public ResponseEntity<String> debateScenario(@PathVariable Long debateId,
                                                 @RequestParam String requestUserId) {
        log.debug("test debate.join debateId: {}, requestUserId = {}", debateId,requestUserId);
        DebateRoomResponse roomResponse = debateService.getRoomById(debateId, null);
        roomResponse.users().stream().filter(u -> !u.userId().equals(requestUserId)).forEach(u->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            playingService.handleJoin(debateId, Long.parseLong(u.userId()));
        });

        return ResponseEntity.ok("토론 참여 시나리오가 수행되었습니다.");
    }

    @GetMapping("/api/tests/debate")
    public ResponseEntity<DebateRoomResponse> createDebateScenario(@RequestParam String requestUserId) {

        matchingScenario(Category.POLITICS.name());
        matchingService.handleJoin(Category.POLITICS, Long.valueOf(requestUserId));
        long debateId = debateService.getTestDebateId(Long.valueOf(requestUserId));
        DebateRoomResponse roomResponse = debateService.getRoomById(debateId, null);
        scheduler.scheduleAtInstant(Instant.now().plusSeconds(10),()->debateScenario(debateId, requestUserId));
        return ResponseEntity.ok(roomResponse);
    }
}