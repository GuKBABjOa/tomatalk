package team.overfullow.tolonbgeub.core.async;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@Component
public class PreciseInstantScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    /**
     * 특정 Instant 시각에 Runnable 태스크를 실행하는 메서드
     *
     * @param instant 실행할 정확한 시각 (UTC 기준)
     * @param task 실행할 작업
     */
    public void scheduleAtInstant(Instant instant, Runnable task) {
        Instant now = Instant.now();
        long delayMillis = Duration.between(now, instant).toMillis();

        if (delayMillis <= 0) {
            log.warn("지정된 시간이 이미 지났습니다. 즉시 실행합니다.");
            task.run();
            return;
        }

        log.info("예약된 작업이 {}ms 후 실행됩니다.", delayMillis);

        scheduler.schedule(() -> {
            // 스케줄링으로 먼저 실행한 후, 정확한 시각까지 미세 조정 (스핀락)
            while (Instant.now().isBefore(instant)) {
                LockSupport.parkNanos(100_000); // 0.1ms (100μs) 단위로 대기
            }
            log.info("예약된 작업 실행");
            task.run();
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}

