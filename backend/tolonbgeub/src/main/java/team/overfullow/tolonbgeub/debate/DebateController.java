package team.overfullow.tolonbgeub.debate;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.overfullow.tolonbgeub.auth.UserId;
import team.overfullow.tolonbgeub.core.dto.CursorRequest;
import team.overfullow.tolonbgeub.core.dto.CursorResponse;
import team.overfullow.tolonbgeub.core.dto.SortBy;
import team.overfullow.tolonbgeub.debate.dto.DebateInfoResponse;
import team.overfullow.tolonbgeub.debate.dto.DebateRoomResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.LongStream;

import static java.util.Objects.isNull;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class DebateController {

    private final DebateService debateService;

    @GetMapping("/api/debates/{debateId}/room")
    public ResponseEntity<DebateRoomResponse> getById(@PathVariable Long debateId,
                                                      @AuthenticationPrincipal UserId userId
    ) {
        log.info("토론 방 정보 조회 요청: debateId={}, requestUserId(nullable)={}", debateId, userId);
        Long requestUserId = isNull(userId) ? null : Long.valueOf(userId.value());
        return ResponseEntity.ok(debateService.getRoomById(debateId, requestUserId));
    }

//    @Deprecated
    @GetMapping("/api/debates/{debateId}/fortest")
    public ResponseEntity<List<DebateRoomResponse>> getAllForTest() {
        return ResponseEntity.ok(debateService.getAllForTest());
    }

    /**
     * 진행 중인 토론 목록 조회 API
     */
    @GetMapping("/api/debates")
    public ResponseEntity<CursorResponse<DebateInfoResponse>> getList(@Valid CursorRequest request,
                                                                      @RequestParam @Nullable SortBy sortBy,
                                                                      @RequestParam @Nullable Set<Category> categories,
                                                                      @RequestParam @Nullable DebateStatus status
    ) {
        log.info("진행중인 토론 조회: status = {}, categories = {}", status, categories);
        CursorResponse<DebateInfoResponse> response = CursorResponse.<DebateInfoResponse>builder()
                .content(LongStream.range(0, request.size())
                        .mapToObj(l -> getMockDebateResponse(request.cursor() + l, status))
                        .toList())
                .size(request.size())
                .first(request.cursor() == null)
                .last(false)
                .build();

        return ResponseEntity.ok(response);
    }

    private DebateInfoResponse getMockDebateResponse(Long debateId, DebateStatus status) {
        LocalDateTime now = LocalDateTime.now();
        int anInt = new Random().nextInt(100);
        int hour = now.getHour();
        int min = now.getMinute() - (int) (30f * anInt / 100f);
        if (min < 0) {
            min += 60;
            hour -= 1;
            if (hour < 0) {
                hour = 23;
            }
        }
        return DebateInfoResponse.builder()
                .debateId(debateId.toString())
                .category(Category.values()[(anInt % Category.values().length)].name())
                .subject("논제 예시")
                .status(status)
                .startedAtHour(hour)
                .startedAtMinute(min)
                .estimatedTimeMinute(45)
                .spectatorsCount(new Random().nextInt(100))
                .build();
    }
}