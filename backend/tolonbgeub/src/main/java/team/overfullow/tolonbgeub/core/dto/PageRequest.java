package team.overfullow.tolonbgeub.core.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


public record PageRequest(
        @Nullable @Min(0) Integer page,
        @NotNull @Min(1) @Max(20) Integer size) {

    @Builder
    public PageRequest {
    }
}
