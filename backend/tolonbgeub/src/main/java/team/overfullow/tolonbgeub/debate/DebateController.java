package team.overfullow.tolonbgeub.debate;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.overfullow.tolonbgeub.core.dto.CursorRequest;
import team.overfullow.tolonbgeub.core.dto.CursorResponse;
import team.overfullow.tolonbgeub.core.dto.SortBy;
import team.overfullow.tolonbgeub.debate.dto.DebateResponse;

import java.util.Set;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.LongStream;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class DebateController {

    // todo 토론 방으로 리팩토링
    @GetMapping("/api/debates/{debateId}")
    public ResponseEntity<DebateResponse> getById(@PathVariable Long debateId) {
        return ResponseEntity.ok(getMockDebateResponse(debateId, DebateStatus.IN_PROGRESS));
    }

    /**
     * 진행 중인 토론 목록 조회 API
     */
    @GetMapping("/api/debates")
    public ResponseEntity<CursorResponse<DebateResponse>> getList(@Valid CursorRequest request,
                                                                  @RequestParam @Nullable SortBy sortBy,
                                                                  @RequestParam @Nullable Set<Category> categories,
                                                                  @RequestParam @Nullable DebateStatus status
    ) {
        log.info("진행중인 토론 조회: status = {}, categories = {}", status,categories);
        CursorResponse<DebateResponse> response = CursorResponse.<DebateResponse>builder()
                .content(LongStream.range(0, request.size())
                        .mapToObj(l -> getMockDebateResponse(request.cursor() + l, status))
                        .toList())
                .size(request.size())
                .first(request.cursor() == null)
                .last(false)
                .build();

        return ResponseEntity.ok(response);
    }

    private DebateResponse getMockDebateResponse(Long debateId, DebateStatus status) {
        LocalDateTime now = LocalDateTime.now();
        int anInt = new Random().nextInt(100);
        int hour = now.getHour();
        int min = now.getMinute() - (int)(30f * anInt / 100f);
        if(min<0){
            min+=60;
            hour-=1;
            if (hour<0){
                hour=23;
            }
        }
        return DebateResponse.builder()
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