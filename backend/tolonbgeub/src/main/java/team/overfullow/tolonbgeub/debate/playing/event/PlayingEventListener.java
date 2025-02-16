package team.overfullow.tolonbgeub.debate.playing.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayingEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @EventListener
    public void handleStateUpdate(StateUpdateEvent event) {
        log.info("handle state update event: {}", event);
        messagingTemplate.convertAndSend(
                event.destination(),
                event.payload()
        );
        log.info("sent state update event = {}", event);
    }
}
