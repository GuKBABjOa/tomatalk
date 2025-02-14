package team.overfullow.tolonbgeub.matching.event;

import lombok.Data;

@Data
public class MatchingSuccessEvent {
    private final String userId;
    private final String destination;
    private final Object payload;
}