package team.overfullow.tolonbgeub.game.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Game {
    private String gameId;
    private String topic;
    private String topicDetail;
    private Map<String, Player> players = new HashMap<>();
    private GameStatus status = GameStatus.WAITING;
    private int currentRound = 0;
    private String currentSpeaker;
    private List<String> playerOrder = new ArrayList<>();
    private boolean isFirstPlayerTurn = true;
}