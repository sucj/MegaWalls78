package icu.suc.megawalls78.game;

public enum GameState {
    WAITING(),
    COUNTDOWN(),
    OPENING(),
    PREPARING(),
    BUFFING(),
    FIGHTING(),
    DM(),
    ENDING();

    GameState() {
    }
}
