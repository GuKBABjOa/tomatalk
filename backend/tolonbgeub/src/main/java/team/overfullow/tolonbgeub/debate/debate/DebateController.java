package team.overfullow.tolonbgeub.debate.debate;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.domain.DebateStatus;
import team.overfullow.tolonbgeub.debate.debate.dto.*;
import team.overfullow.tolonbgeub.debate.debate.service.DebateMetricService;
import team.overfullow.tolonbgeub.debate.debate.service.DebateService;
import team.overfullow.tolonbgeub.debate.playing.PlayingService;
import team.overfullow.tolonbgeub.debate.playing.message.response.PlayingUserResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class DebateController {

    private final DebateService debateService;
    private final DebateMetricService debateMetricService;
    private final PlayingService playingService;

    @GetMapping("/api/debates/{debateId}/room")
    public ResponseEntity<DebateRoomResponse> getById(@PathVariable Long debateId,
                                                 @AuthenticationPrincipal UserId userId
    ) {
        log.debug("토론 방 정보 조회 요청: debateId={}, requestUserId(nullable)={}", debateId, userId);
        Long requestUserId = isNull(userId) ? null : Long.valueOf(userId.value());
        playingService.forceInit(debateId);
        List<PlayingUserResponse> playingUsers = playingService.getPlayingUsers(debateId);
        DebateRoomDto dto = debateService.getRoomById(debateId, requestUserId);
        return ResponseEntity.ok(DebateRoomResponse.fromDto(dto, playingUsers));
    }
    /**
     * 토론 목록 페이징 및 필터링 API
     */
    @GetMapping("/api/debates")
    public ResponseEntity<CursorResponse<DebateInfoResponse>> getPage(@Valid CursorRequest request,
                                                                      @RequestParam @Nullable SortBy sortBy,
                                                                      @RequestParam @Nullable Set<Category> categories,
                                                                      @RequestParam @Nullable DebateStatus status,
                                                                      @RequestParam @Nullable String keyword
    ) {
        log.debug("진행중인 토론 조회: request = {}, sortBy = {}, status = {}, categories = {}, keyword = {}", request, sortBy, status, categories, keyword);
        request = CursorRequest.builder()
                .size(100)
                .build();
        Page<DebateInfoResponse> page = debateService.search(request, sortBy, categories, status, keyword);
        return ResponseEntity.ok(toCursorResponse(page, request));
    }

    @GetMapping("/api/debates/aggregates/category")
    public ResponseEntity<List<CategoryDebateCount>> getCategoryDebateCount() {
        return ResponseEntity.ok(debateService.countInProgressDebatesByCategory());
    }

    private CursorResponse<DebateInfoResponse> toCursorResponse(
            Page<DebateInfoResponse> page,
            CursorRequest cursorRequest
    ) {
        List<DebateInfoResponse> content = page.getContent().stream()
                .map(this::remakeDebateInfoResponse)
                .toList();
        return CursorResponse.<DebateInfoResponse>builder()
                .content(content)
                .size(content.size())
                .first(cursorRequest.cursor() == null)
                .last(!page.hasNext())
                .build();
    }

    private DebateInfoResponse remakeDebateInfoResponse(DebateInfoResponse debate) {
        LocalDateTime createdAt = debate.createdAt();
        boolean isInProgress = debate.status() == DebateStatus.IN_PROGRESS;
        return DebateInfoResponse.builder()
                .debateId(debate.debateId())
                .category(debate.category())
                .subject(debate.subject())
                .status(debate.status())
                .startedAtHour(createdAt.getHour()) // WAITING->STARTED 로 상태 바뀔 때,별도로 계산하는 게 좋을 거 같음
                .startedAtMinute(createdAt.getMinute())
                .estimatedTimeMinute(isInProgress ? debateMetricService.getEstimatedTimeMinute(Long.valueOf(debate.debateId())) : 0)
                .spectatorsCount(isInProgress ? debateMetricService.getSpectatorsCount(Long.valueOf(debate.debateId())) : 0)
                .createdAt(debate.createdAt())
                .lastModifiedAt(debate.lastModifiedAt())
                .build();
    }
}