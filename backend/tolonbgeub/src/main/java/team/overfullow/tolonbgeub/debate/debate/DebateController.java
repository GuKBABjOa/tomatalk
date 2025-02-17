package team.overfullow.tolonbgeub.debate.debate;

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
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.domain.DebateStatus;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateInfoResponse;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateRoomResponse;
import team.overfullow.tolonbgeub.debate.debate.service.DebateService;

import java.util.Set;

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
        log.debug("토론 방 정보 조회 요청: debateId={}, requestUserId(nullable)={}", debateId, userId);
        Long requestUserId = isNull(userId) ? null : Long.valueOf(userId.value());
        return ResponseEntity.ok(debateService.getRoomById(debateId, requestUserId));
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
        return ResponseEntity.ok(debateService.search(request, sortBy, categories, status, keyword));
    }
}