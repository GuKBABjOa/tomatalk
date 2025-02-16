package team.overfullow.tolonbgeub.column;

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
import team.overfullow.tolonbgeub.column.dto.ColumnResponse;
import team.overfullow.tolonbgeub.core.dto.CursorRequest;
import team.overfullow.tolonbgeub.core.dto.CursorResponse;
import team.overfullow.tolonbgeub.core.dto.SortBy;
import team.overfullow.tolonbgeub.debate.Category;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.stream.LongStream;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ColumnController {

    @GetMapping("/api/columns/{columnId}")
    public ResponseEntity<ColumnResponse> getById(@PathVariable Long columnId) {
        return ResponseEntity.ok(getMockColumnResponse(columnId));
    }

    @GetMapping("/api/columns")
    public ResponseEntity<CursorResponse<ColumnResponse>> getList(@Valid CursorRequest request,
                                                                  @RequestParam @Nullable SortBy sortBy,
                                                                  @RequestParam @Nullable Set<Category> categories
    ) {
        long cursor = request.cursor() == null ? 0 : request.cursor();
        CursorResponse<ColumnResponse> response = CursorResponse.<ColumnResponse>builder()
                .content(LongStream.range(0, request.size())
                        .mapToObj(l -> getMockColumnResponse(cursor + l))
                        .toList())
                .size(request.size())
                .first(request.cursor() == null)
                .last(false)
                .build();

        return ResponseEntity.ok(response);
    }

    private ColumnResponse getMockColumnResponse(Long columnId) {
        LocalDateTime now = LocalDateTime.now();
        int anInt = new Random().nextInt(1000);
        return ColumnResponse.builder()
                .columnId(columnId.toString())
                .title("칼럼 제목" + anInt)
                .summary("칼럼 요약" + anInt)
                .createdAt(now)
                .bookmarkCount(anInt/10 * anInt/10)
                .category(Category.values()[(anInt % Category.values().length)].name())
                .build();
    }
}