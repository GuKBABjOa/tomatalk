package team.overfullow.tolonbgeub.debate;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.overfullow.tolonbgeub.core.dto.CursorRequest;
import team.overfullow.tolonbgeub.core.dto.CursorResponse;
import team.overfullow.tolonbgeub.debate.dto.DebateResponse;

import java.time.LocalDateTime;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class DebateController {

    // mock
    @GetMapping("/api/debates/{debateId}")
    public ResponseEntity<DebateResponse> getById(@PathVariable Long debateId) {
        return ResponseEntity.ok(getMockDebateResponse(debateId, DebateStatus.IN_PROGRESS));
    }

    @GetMapping("/api/debates")
    public ResponseEntity<CursorResponse<DebateResponse>> getList(@Valid CursorRequest request,
                                                                  @RequestParam @Nullable DebateStatus status) {
        log.info("status = {}", status);
        CursorResponse<DebateResponse> response = CursorResponse.<DebateResponse>builder()
                .content(LongStream.range(0, request.size())
                        .mapToObj(l -> getMockDebateResponse(request.cursor()+l, DebateStatus.IN_PROGRESS))
                        .toList())
                .size(request.size())
                .first(request.cursor() == null)
                .last(false)
                .build();

        return ResponseEntity.ok(response);
    }

    private DebateResponse getMockDebateResponse(Long debateId, DebateStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return DebateResponse.builder()
                .debateId(debateId.toString())
                .subject("논제 예시")
                .status(status)
                .startedAtHour(now.getHour())
                .startedAtMinute(now.getMinute())
                .estimatedTimeMinute(45)
                .build();
    }
}