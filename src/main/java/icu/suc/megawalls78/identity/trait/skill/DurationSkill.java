package icu.suc.megawalls78.identity.trait.skill;

import icu.suc.megawalls78.identity.trait.passive.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class DurationSkill extends Skill {

    protected long DURATION;

    long DURATION_LAST;

    public DurationSkill(String id, int cost, long cooldown, long duration) {
        this(id, cost, cooldown, duration, null);
    }

    public DurationSkill(String id, int cost, long cooldown, long duration, Class<? extends Passive> internal) {
        super(id, cost, cooldown, internal);
        this.DURATION = duration;
    }

    @Override
    public boolean use(Player player) {
        if (!available()) {
            return false;
        }
        if (COOLDOWN()) {
            if (use0(player)) {
                DURATION_RESET();
                return true;
            }
        }
        return false;
    }

    public long getDuration() {
        return DURATION;
    }

    protected boolean DURATION() {
        return CURRENT() - DURATION_LAST() <= DURATION;
    }

    protected long DURATION(long delta) {
        COOLDOWN(-delta);
        return DURATION_LAST -= delta;
    }

    protected long DURATION_LAST() {
        return DURATION_LAST;
    }

    protected void DURATION_RESET() {
        long current = CURRENT();
        COOLDOWN_LAST = current;
        DURATION_LAST = current;
        COOLDOWN(DURATION);
    }

    protected void DURATION_END() {
        long remain = DURATION_REMAIN();
        if (remain > 0) {
            COOLDOWN(-remain);
        }
        DURATION_LAST = 0;
    }

    protected long DURATION_REMAIN() {
        return DURATION - CURRENT() + DURATION_LAST();
    }

    @Override
    public Component acb() {
        return Type.DURATION_COOLDOWN_STATE.accept(COOLDOWN_REMAIN(), DURATION_REMAIN(), available());
    }

    public void stop() {
        DURATION_END();
    }
}
