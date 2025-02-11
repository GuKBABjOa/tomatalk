package team.overfullow.tolonbgeub.core.dto;

import lombok.Builder;

import java.util.List;

public record PageResponse<T>(Integer currentPage,
                              Integer size,
                              Integer totalPage,
                              List<T> content) {
    @Builder
    public PageResponse {
    }
}
