package team.overfullow.tolonbgeub.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import team.overfullow.tolonbgeub.game.domain.GameStatus;
import team.overfullow.tolonbgeub.game.dto.JoinRequest;
import team.overfullow.tolonbgeub.game.message.GameMessage;
import team.overfullow.tolonbgeub.game.message.MessageType;
import team.overfullow.tolonbgeub.game.message.payload.CountDown;
import team.overfullow.tolonbgeub.game.message.payload.GameState;
import team.overfullow.tolonbgeub.game.message.payload.Greeting;
import team.overfullow.tolonbgeub.webrtc.OpenViduHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameController {
    private static final String DESTINATION_PREFIX = "/sub/game/";
    private static final int SECONDS_TURN = 25;
    private static final int SECONDS_COUNT_DOWN = 5;

    private final GameService gameService;
    private final OpenViduHandler openviduHandler;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/games/sample/{gameId}")
    public ResponseEntity<String> getTampToken(@PathVariable String gameId){
        return ResponseEntity.ok(openviduHandler.createConnectionToken(gameId));
    }

    // 게임 참여
    @MessageMapping("/game.join/{gameId}")
    public void joinGame(@DestinationVariable String gameId, JoinRequest request) {
        log.info("game join request for gameId = {}", gameId);
        log.info("game join request = {}", request);

        GameState state = gameService.joinGame(gameId, request.getUserId());
        String connectionToken = openviduHandler.createConnectionToken(gameId);
//        String connectionToken = "sampleToken";
        messagingTemplate.convertAndSend(DESTINATION_PREFIX + gameId,
                new GameMessage(MessageType.GREETING, Greeting.builder()
                        .greetingMessage("Hello, " + request.getUserId() + "!")
                        .connectionToken(connectionToken)
                        .build()));


        switch (state.getStatus()) {
            case WAITING -> messagingTemplate.convertAndSend(DESTINATION_PREFIX + gameId,
                    new GameMessage(MessageType.WAITING_PLAYER, state));
            case READY -> startGameSequence(gameId);
            default -> throw new IllegalStateException("Unexpected value: " + state.getStatus());
        }
    }

    // 게임 시작 시퀀스
    private void startGameSequence(String gameId) {
        log.info("start game sequence for gameId = {}", gameId);
        // 1. 게임 시작 알림
        messagingTemplate.convertAndSend(DESTINATION_PREFIX + gameId,
                new GameMessage(
                        MessageType.GAME_START,
                        gameService.initializeGame(gameId)));

        // 2. 주제 및 입장 발표
        messagingTemplate.convertAndSend(DESTINATION_PREFIX + gameId,
                new GameMessage(
                        MessageType.TOPIC_ANNOUNCE,
                        gameService.announceTopicAndStances(gameId)));

        // 3. 5초 카운트다운 시작
        startCountdown(gameId, SECONDS_COUNT_DOWN, () -> startGameRounds(gameId));
    }

    // 카운트다운 처리
    private void startCountdown(String gameId, int seconds, Runnable onComplete) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger remainingSeconds = new AtomicInteger(seconds);

        executor.scheduleAtFixedRate(() -> {
            if (remainingSeconds.get() > 0) {
                messagingTemplate.convertAndSend(DESTINATION_PREFIX + gameId,
                        new GameMessage(MessageType.COUNT_DOWN, CountDown.builder()
                                .totalTime(seconds)
                                .remainingTime(remainingSeconds.get())
                                .build()));
                remainingSeconds.decrementAndGet();
            } else {
                executor.shutdown();
                onComplete.run();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    // 게임 라운드 진행
    private void startGameRounds(String gameId) {
        runTurnSequence(gameId, gameService.startRound(gameId));
    }

    // 턴 진행
    private void runTurnSequence(String gameId, GameState state) {
        // x초 턴 타이머 시작
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger remainingSeconds = new AtomicInteger(SECONDS_TURN);

        executor.scheduleAtFixedRate(() -> {
            if (remainingSeconds.get() > 0) {
                state.setTotalTime(SECONDS_TURN);
                state.setRemainingTime(remainingSeconds.get());
                messagingTemplate.convertAndSend(DESTINATION_PREFIX + gameId,
                        new GameMessage(MessageType.TURN_UPDATE, state));
                remainingSeconds.decrementAndGet();
            } else {
                executor.shutdown();
                handleTurnEnd(gameId);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    // 턴 종료 처리
    private void handleTurnEnd(String gameId) {
        GameState state = gameService.endTurn(gameId);

        if (state.getStatus() == GameStatus.FINISHED) {
            // 게임 종료
            messagingTemplate.convertAndSend(DESTINATION_PREFIX + gameId,
                    new GameMessage(MessageType.GAME_END, state));
            // 5초 후 연결 종료
            scheduleDisconnect(gameId);
        } else {
            // 3초 대기 후 다음 턴
            startCountdown(gameId, 3, () -> runTurnSequence(gameId, state));
        }
    }

    // 연결 종료 스케줄링
    private void scheduleDisconnect(String gameId) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            gameService.closeGame(gameId);
        }, 5, TimeUnit.SECONDS);
    }
}