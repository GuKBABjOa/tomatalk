package team.overfullow.tolonbgeub.debate.debate.repository;

import org.springframework.data.domain.Page;
import team.overfullow.tolonbgeub.core.dto.CursorRequest;
import team.overfullow.tolonbgeub.core.dto.SortBy;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.domain.Debate;
import team.overfullow.tolonbgeub.debate.debate.domain.DebateStatus;

import java.util.Set;

public interface DebateQueryRepository {
    /**
     * 토론 목록을 커서 기반으로 조회합니다.
     *
     * @param cursorRequest 마지막으로 읽은 데이터의 ID와 조회할 데이터 수
     * @param sortBy        정렬 기준
     * @param categories    카테고리 필터
     * @param status        토론 상태 필터
     * @param keyword       검색어
     * @return 토론 목록 페이지
     */
    Page<Debate> searchByCursor(
            CursorRequest cursorRequest,
            SortBy sortBy,
            Set<Category> categories,
            DebateStatus status,
            String keyword
    );
}
