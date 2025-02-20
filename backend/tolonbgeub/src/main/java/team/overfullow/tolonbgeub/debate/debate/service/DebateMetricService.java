package team.overfullow.tolonbgeub.debate.debate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.overfullow.tolonbgeub.debate.playing.observer.DebateObserverManager;

@Component
@RequiredArgsConstructor
public class DebateMetricService {
    private final DebateObserverManager debateObserverManager;

    public Integer getEstimatedTimeMinute(Long debateId) {
        return 45;
    }

    public Integer getSpectatorsCount(Long debateId) {
        return debateObserverManager.getObserverCount(debateId.toString());
    }
}
