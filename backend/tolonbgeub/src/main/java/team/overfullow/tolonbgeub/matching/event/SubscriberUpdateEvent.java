package team.overfullow.tolonbgeub.matching.event;

import lombok.Data;

@Data
public class SubscriberUpdateEvent {
    private final String destination;
    private final Object payload;
}