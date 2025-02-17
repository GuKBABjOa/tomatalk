package team.overfullow.tolonbgeub.core.dto;

import lombok.Builder;

import java.util.List;

public record CursorResponse<T>(List<T> content,
                                int size,
                                boolean first,
                                boolean last
                                ) {
    @Builder
    public CursorResponse {
    }
}
