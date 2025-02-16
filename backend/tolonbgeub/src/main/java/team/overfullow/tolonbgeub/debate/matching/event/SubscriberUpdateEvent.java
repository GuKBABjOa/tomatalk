package team.overfullow.tolonbgeub.debate.matching.event;

import lombok.Data;

@Data
public class SubscriberUpdateEvent {
    private final String destination;
    private final Object payload; //todo 응답 메시지 타입으로 변경
}