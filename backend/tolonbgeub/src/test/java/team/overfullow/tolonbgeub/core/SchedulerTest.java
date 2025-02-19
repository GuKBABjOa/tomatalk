package team.overfullow.tolonbgeub.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import team.overfullow.tolonbgeub.core.util.PreciseInstantScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class SchedulerTest {

    @Nested
    @DisplayName("PreciseInstantScheduler 테스트")
    class PreciseInstantSchedulerTest {

        @Test
        void testScheduleAtInstant_정확한_시간에_실행되는지_검증() throws InterruptedException {
            // given
            PreciseInstantScheduler sut = new PreciseInstantScheduler();
            AtomicLong actualExecutionTime = new AtomicLong();
            Instant executionTime = Instant.now().plus(1, ChronoUnit.SECONDS);

            // when
            sut.scheduleAtInstant(executionTime,
                    () -> actualExecutionTime.set(System.currentTimeMillis()));

            Thread.sleep(1500);

            // then
            long expectedMillis = executionTime.toEpochMilli();
            long actualMillis = actualExecutionTime.get();

            log.info("오차 = {}ms", actualMillis - expectedMillis);
            // 실행 시각과 목표 시각의 오차가 ±100ms 이하인지 확인
            assertThat(actualMillis)
                    .isGreaterThanOrEqualTo(expectedMillis - 10)
                    .isLessThanOrEqualTo(expectedMillis + 10);
        }

        @Test
        void testScheduleAtInstant_이미_지난_시간이면_즉시_실행() {
            // given
            PreciseInstantScheduler sut = new PreciseInstantScheduler();
            // 과거의 Instant (현재 시각에서 1초 전)
            Instant pastTime = Instant.now().minus(1, ChronoUnit.SECONDS);
            AtomicLong executionTime = new AtomicLong();

            // when
            sut.scheduleAtInstant(pastTime, () -> executionTime.set(System.currentTimeMillis()));

            // then
            // 즉시 실행되었는지 확인
            assertThat(executionTime.get()).isNotZero();
        }

        @Test
        @DisplayName("동시에 1만개의 작업을 스케줄링 시, 평균 오차 시간은 100ms 이내여야 한다.")
        void testScheduleAtInstant_동시_n개_작업_실행_오차_검증() throws InterruptedException {
            // given
            PreciseInstantScheduler sut = new PreciseInstantScheduler();
            int taskCount = 10000;
            int millis = 1000;
            int targetErrorMillis = 100;
            Instant scheduledTime = Instant.now().plusMillis(millis); // 3초 후 실행
            long expectedMillis = scheduledTime.toEpochMilli();
            CountDownLatch latch = new CountDownLatch(taskCount);
            List<Long> executionTimes = new CopyOnWriteArrayList<>();
            ExecutorService executors = Executors.newFixedThreadPool(32);

            // when

            for (int i = 0; i < taskCount; i++) {
                executors.execute(() -> {
                    sut.scheduleAtInstant(scheduledTime, () ->
                            executionTimes.add(System.currentTimeMillis()));
                    latch.countDown();
                });
            }
            Thread.sleep(millis+1000);
            latch.await();

            // then
            // 각 작업이 목표 시각과 ±20ms 이내인지 검증
            // 오차 계산
            List<Long> errors = new ArrayList<>();
            for (long actualMillis : executionTimes) {
                errors.add(Math.abs(actualMillis - expectedMillis));
            }

            // 평균, 최대, 최소 오차 계산
            long maxError = errors.stream().max(Long::compare).orElse(0L);
            long minError = errors.stream().min(Long::compare).orElse(0L);
            double avgError = errors.stream().mapToLong(Long::longValue).average().orElse(0);

            // 결과 출력
            System.out.println("errors.size() = " + errors.size());
            System.out.println("Average Error: " + avgError + " ms");
            System.out.println("Max Error: " + maxError + " ms");
            System.out.println("Min Error: " + minError + " ms");

            // 모든 실행 시각이 ±20ms 이내인지 검증
            assertThat(errors.size()).isEqualTo(taskCount);
            assertThat(maxError).isLessThanOrEqualTo(targetErrorMillis);
            assertThat(minError).isLessThanOrEqualTo(targetErrorMillis);
            assertThat(avgError).isLessThanOrEqualTo(targetErrorMillis);
        }
    }
}