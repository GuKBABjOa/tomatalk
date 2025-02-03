package team.overfullow.tolonbgeub.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import team.overfullow.tolonbgeub.game.domain.GameStatus;
import team.overfullow.tolonbgeub.game.domain.MessageType;
import team.overfullow.tolonbgeub.game.dto.GameMessage;
import team.overfullow.tolonbgeub.game.dto.GameStateDto;
import team.overfullow.tolonbgeub.game.dto.JoinRequest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    // 게임 참여
    @MessageMapping("/game.join/{gameId}")
    public void joinGame(@DestinationVariable String gameId, JoinRequest request) throws JsonProcessingException {
        log.info("game join request for gameId = {}", gameId);
        log.info("game join request = {}", request);

        GameStateDto state = gameService.joinGame(gameId, request.getUserId());
        // todo openvidu connection token 생성해서 전달
        messagingTemplate.convertAndSend("/topic/game/" + gameId, "Hello, " + request.getUserId() + "!");

        switch (state.getStatus()) {
            case WAITING -> messagingTemplate.convertAndSend("/topic/game/" + gameId,
                    new GameMessage(MessageType.WAITING_PLAYER, state));
            case READY -> startGameSequence(gameId);
            default -> throw new IllegalStateException("Unexpected value: " + state.getStatus());
        }
    }

    // 게임 시작 시퀀스
    private void startGameSequence(String gameId) {
        // 1. 게임 시작 알림
        messagingTemplate.convertAndSend("/topic/game/" + gameId,
                new GameMessage(
                        MessageType.GAME_START,
                        gameService.initializeGame(gameId)));

        // 2. 주제 및 입장 발표
        messagingTemplate.convertAndSend("/topic/game/" + gameId,
                new GameMessage(
                        MessageType.TOPIC_ANNOUNCE,
                        gameService.announceTopicAndStances(gameId)));

        // 3. 5초 카운트다운 시작
        startCountdown(gameId, 5, () -> startGameRounds(gameId));
    }

    // 카운트다운 처리
    private void startCountdown(String gameId, int seconds, Runnable onComplete) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger remainingSeconds = new AtomicInteger(seconds);

        executor.scheduleAtFixedRate(() -> {
            if (remainingSeconds.get() > 0) {
                messagingTemplate.convertAndSend("/topic/game/" + gameId,
                        new GameMessage(MessageType.COUNT_DOWN, remainingSeconds.get()));
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
    private void runTurnSequence(String gameId, GameStateDto state) {
        // 30초 턴 타이머 시작
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger remainingSeconds = new AtomicInteger(30);

        executor.scheduleAtFixedRate(() -> {
            if (remainingSeconds.get() > 0) {
                state.setRemainingTime(remainingSeconds.get());
                messagingTemplate.convertAndSend("/topic/game/" + gameId,
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
        GameStateDto state = gameService.endTurn(gameId);

        if (state.getStatus() == GameStatus.FINISHED) {
            // 게임 종료
            messagingTemplate.convertAndSend("/topic/game/" + gameId,
                    new GameMessage(MessageType.GAME_END,  state));
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