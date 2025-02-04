package team.overfullow.tolonbgeub.game.message;

public enum MessageType {
    GREETING,           // 게임 참여 성공
    WAITING_PLAYER,     // 상대 플레이어의 게임 참여 대기
    GAME_READY,         // 게임 준비 완료
    TOPIC_ANNOUNCE,     // 주제 및 입장 발표
    GAME_START,         // 게임 시작
    COUNT_DOWN,         // 카운트다운
    TURN_START,         // 턴 시작
    TURN_UPDATE,        // 턴 진행 상태 업데이트
    TURN_END,           // 턴 종료
    GAME_END,           // 게임 종료
    PLAYER_DISCONNECT,  //플레이어 연결 종료
}