package team.overfullow.tolonbgeub.column.dto;

import lombok.Builder;
import team.overfullow.tolonbgeub.debate.Category;

import java.time.LocalDateTime;

public record ColumnResponse(
        String columnId,
        String title,
        String summary,
        Integer bookmarkCount,
        LocalDateTime createdAt,
        String category
){
    @Builder
    public ColumnResponse {
    }
}