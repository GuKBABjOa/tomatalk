package team.overfullow.tolonbgeub.debate.debate.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.overfullow.tolonbgeub.core.dto.CursorRequest;
import team.overfullow.tolonbgeub.core.dto.SortBy;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.domain.Debate;
import team.overfullow.tolonbgeub.debate.debate.domain.DebateStatus;
import team.overfullow.tolonbgeub.debate.debate.dto.CategoryDebateCount;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;
import static team.overfullow.tolonbgeub.debate.debate.domain.QDebate.debate;

@Repository
@RequiredArgsConstructor
public class DebateQueryRepositoryImpl implements DebateQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 각 Category별 진행중인(IN_PROGRESS) 토론방 개수를 조회합니다.
     * @return Category별 토론방 개수가 담긴 DTO 리스트
     */
    public List<CategoryDebateCount> countInProgressDebatesByCategory() {
        return queryFactory
                .select(Projections.constructor(CategoryDebateCount.class,
                        debate.category,
                        debate.count()))
                .from(debate)
                .where(debate.status.eq(DebateStatus.IN_PROGRESS))
                .groupBy(debate.category)
                .fetch();
    }

    @Override
    public Page<Debate> searchByCursor(
            CursorRequest cursorRequest,
            SortBy sortBy,
            Set<Category> categories,
            DebateStatus status,
            String keyword
    ) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, cursorRequest.size());

        List<Debate> debates = queryFactory
                .selectFrom(debate)
                .join(debate.subject).fetchJoin()  // N+1 문제 방지를 위한 페치 조인
                .where(
                        cursorLt(cursorRequest.cursor()),
                        categoriesIn(categories),
                        statusEq(status),
                        subjectContains(keyword)
                )
                .orderBy(createOrderSpecifier(sortBy))
                .limit(cursorRequest.size())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(debate.count())
                .from(debate)
                .where(lastCursorLt(debates));
        return PageableExecutionUtils.getPage(debates, pageable, countQuery::fetchOne);

    }

    private static BooleanExpression lastCursorLt(List<Debate> debates) {
        return debates.isEmpty() ? null : debate.id.lt(debates.get(debates.size() - 1).getId());
    }

    // 커서 기반 페이징을 위한 ID 비교 조건
    private BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? debate.id.lt(cursor) : null;
    }

    // 카테고리 필터링 조건
    private BooleanExpression categoriesIn(Set<Category> categories) {
        return categories != null && !categories.isEmpty() ?
                debate.category.in(categories) : null;
    }

    // 토론 상태 필터링 조건
    private BooleanExpression statusEq(DebateStatus status) {
        return status != null ? debate.status.eq(status) : null;
    }

    // 논제 검색 조건
    private BooleanExpression subjectContains(String keyword) {
        return hasText(keyword) ? debate.subject.subject.containsIgnoreCase(keyword) : null;
    }

    // 정렬 조건 생성
    private OrderSpecifier<?> createOrderSpecifier(SortBy sortBy) {
        if (Objects.isNull(sortBy)) {
            return new OrderSpecifier<>(Order.DESC, debate.createdAt);
        }
        return switch (sortBy) {
            case LATEST -> new OrderSpecifier<>(Order.DESC, debate.createdAt);
            case POPULARITY -> new OrderSpecifier<>(Order.DESC, debate.createdAt); // todo
        };
    }
}