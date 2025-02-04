package team.overfullow.tolonbgeub.game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.overfullow.tolonbgeub.game.domain.Game;
import team.overfullow.tolonbgeub.game.domain.GameStatus;
import team.overfullow.tolonbgeub.game.domain.Player;
import team.overfullow.tolonbgeub.game.message.payload.GameState;
import team.overfullow.tolonbgeub.game.message.payload.StanceInfo;
import team.overfullow.tolonbgeub.game.exception.GameErrorCode;
import team.overfullow.tolonbgeub.game.exception.GameException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GameService {
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    // 게임 참여 처리
    public GameState joinGame(String gameId, String userId) {
        // todo userId 검증

        Game game = games.computeIfAbsent(gameId, id -> {
            Game newGame = new Game();
            newGame.setGameId(id);

            // todo openvidu session 초기화
            return newGame;
        });

        synchronized (game) {
            if (game.getPlayers().size() >= 2) {
                throw new GameException(GameErrorCode.GAME_FULL, "Game is already full");
            }

            Player player = new Player();
            player.setUserId(userId);
            game.getPlayers().put(userId, player);

            if (game.getPlayers().size() == 2) {
                game.setStatus(GameStatus.READY);
            }

            return convertToGameState(game);
        }
    }

    // 게임 초기화
    public GameState initializeGame(String gameId) {
        Game game = getGame(gameId);
        synchronized (game) {
            // 주제 설정 (Mocking)
            game.setTopic("월 200 백수 VS 월 600 개발자");
            game.setTopicDetail("하나만 고를 수 있다면? 월 200 백수 VS 월 600 개발자");

            // 플레이어 순서 무작위 설정
            List<String> playerIds = new ArrayList<>(game.getPlayers().keySet());
            Collections.shuffle(playerIds);
            game.setPlayerOrder(playerIds);

            // 입장(stance) 설정
            game.getPlayers().get(playerIds.get(0)).setStance("O");
            game.getPlayers().get(playerIds.get(0)).setTurn(true);
            game.getPlayers().get(playerIds.get(1)).setStance("X");
            game.getPlayers().get(playerIds.get(1)).setTurn(false);

            game.setStatus(GameStatus.PLAYING);
            return convertToGameState(game);
        }
    }

    // 주제 및 입장 발표
    public GameState announceTopicAndStances(String gameId) {
        Game game = getGame(gameId);
        synchronized (game) {
            game.setCurrentRound(1);
            game.setCurrentSpeaker(game.getPlayerOrder().get(0));
            game.setFirstPlayerTurn(true);
            return convertToGameState(game);
        }
    }

    // 라운드 시작
    public GameState startRound(String gameId) {
        Game game = getGame(gameId);
        synchronized (game) {
            if (game.getCurrentRound() > 3) {
                game.setStatus(GameStatus.FINISHED);
            }
            return convertToGameState(game);
        }
    }

    // 턴 종료 처리
    public GameState endTurn(String gameId) {
        Game game = getGame(gameId);
        synchronized (game) {
            if (game.isFirstPlayerTurn()) {
                // 첫 번째 플레이어의 턴이 끝남
                game.setFirstPlayerTurn(false);
                game.setCurrentSpeaker(game.getPlayerOrder().get(1));
            } else {
                // 두 번째 플레이어의 턴이 끝남, 다음 라운드로
                game.setFirstPlayerTurn(true);
                game.setCurrentSpeaker(game.getPlayerOrder().get(0));
                game.setCurrentRound(game.getCurrentRound() + 1);
            }

            if (game.getCurrentRound() > 3) {
                game.setStatus(GameStatus.FINISHED);
            }

            return convertToGameState(game);
        }
    }

    // 게임 종료
    public void closeGame(String gameId) {
        games.remove(gameId);
    }

    // 게임 상태 변환
    private GameState convertToGameState(Game game) {
        GameState state = new GameState();
        state.setGameId(game.getGameId());
        state.setTopic(game.getTopic());
        state.setTopicDetail(game.getTopicDetail());
        state.setCurrentRound(game.getCurrentRound());
        state.setCurrentSpeaker(game.getCurrentSpeaker());
        state.setStatus(game.getStatus());

        Map<String, StanceInfo> playerStances = new HashMap<>();
        game.getPlayers().forEach((userId, player) -> {
            StanceInfo stanceInfo = new StanceInfo();
            stanceInfo.setStance(player.getStance());
            stanceInfo.setTurn(player.isTurn());
            playerStances.put(userId, stanceInfo);
        });
        state.setPlayerStances(playerStances);


//        GameStateRecord state = GameStateRecord.builder()
//                .gameId(game.getGameId())
//                .topic(game.getTopic())
//                .topicDetail(game.getTopicDetail())
//                .currentRound(game.getCurrentRound())
//                .currentSpeaker(game.getCurrentSpeaker())
//                .status(game.getStatus())
//                .playerStances(playerStances)
//                .build();

        return state;
    }

    private Game getGame(String gameId) {
        return Optional.ofNullable(games.get(gameId))
                .orElseThrow(() -> new GameException(GameErrorCode.GAME_NOT_FOUND,
                        "Game not found: " + gameId));
    }

    /**
     * 플레이어 연결 해제 처리
     * - 게임에서 플레이어 제거
     * - 게임 상태 업데이트
     * - 남은 플레이어에게 알림용 게임 상태 반환
     */
    public GameState handlePlayerDisconnect(String gameId, String userId) {
        Game game = getGame(gameId);

        synchronized (game) {
            Player disconnectedPlayer = game.getPlayers().remove(userId);
            if (disconnectedPlayer == null) {
                throw new GameException(GameErrorCode.INVALID_PLAYER,
                        "Player not found in game: " + userId);
            }

            // 플레이어 순서 목록에서 제거
            game.getPlayerOrder().remove(userId);

            // 게임 상태 업데이트
            updateGameStateAfterDisconnect(game);

            log.info("Player {} disconnected from game {}. Current player count: {}",
                    userId, gameId, game.getPlayers().size());

            return convertToGameState(game);
        }
    }

    /**
     * 연결 해제 후 게임 상태 업데이트
     * - 플레이어가 없으면 게임 종료
     * - 한 명만 남으면 승자 처리
     */
    private void updateGameStateAfterDisconnect(Game game) {
        if (game.getPlayers().isEmpty()) {
            // 모든 플레이어가 나간 경우
            game.setStatus(GameStatus.FINISHED);
            games.remove(game.getGameId());
        } else if (game.getPlayers().size() == 1) {
            // 한 플레이어만 남은 경우
            game.setStatus(GameStatus.FINISHED);
            String remainingPlayerId = game.getPlayers().keySet().iterator().next();
            Player winner = game.getPlayers().get(remainingPlayerId);
            winner.setWinner(true);  // Player 클래스에 winner 필드 추가 필요

            // 게임 종료 타이머 시작
            scheduleGameClose(game.getGameId());
        }
    }

    /**
     * 게임 종료 스케줄링
     * - 5초 후 게임 리소스 정리
     */
    private void scheduleGameClose(String gameId) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            games.remove(gameId);
            log.info("Game {} has been closed and removed", gameId);
            executor.shutdown();
        }, 5, TimeUnit.SECONDS);
    }
}