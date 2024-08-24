package icu.suc.megawalls78.identity;

public enum EnergyWay {
    MELEE_PER("melee_per"),
    BOW_PER("bow_per"),
    MELEE_WHEN("melee_when"),
    BOW_WHEN("bow_when"),
    PREPARATION("preparation"),
    GAME("game"),
    DM("dm");

    private final String id;

    EnergyWay(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
