package team.overfullow.tolonbgeub.core.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(String message) {
}
