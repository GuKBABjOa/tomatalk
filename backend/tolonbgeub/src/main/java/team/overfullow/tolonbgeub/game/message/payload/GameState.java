package team.overfullow.tolonbgeub.game.message.payload;

import lombok.Data;
import team.overfullow.tolonbgeub.game.domain.GameStatus;

import java.util.Map;

@Data
public class GameState {
    private String gameId;
    private String topic; // 주제
    private String topicDetail; // 주제 상세 설명
    private int currentRound; // 현재 라운드
    private String currentSpeaker; // 현재 발언자 명
    private int totalTime;
    private int remainingTime; // 남은 발언 시간
    private GameStatus status; // 현재 게임 상태
    private Map<String, StanceInfo> playerStances; // 주제에 대한 각 플레이어 별 입장 정보
}
