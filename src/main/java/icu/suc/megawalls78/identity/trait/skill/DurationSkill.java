package icu.suc.megawalls78.identity.trait.skill;

import icu.suc.megawalls78.management.TraitManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class DurationSkill extends Skill {

    long DURATION_LAST;

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

    protected long DURATION_GET() {
        return TraitManager.duration(getClass());
    }

    protected boolean DURATION() {
        return CURRENT() - DURATION_LAST() <= DURATION_GET();
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
        COOLDOWN(DURATION_GET());
    }

    protected void DURATION_END() {
        long remain = DURATION_REMAIN();
        if (remain > 0) {
            COOLDOWN(-remain);
        }
        DURATION_LAST = 0;
    }

    protected long DURATION_REMAIN() {
        return DURATION_GET() - CURRENT() + DURATION_LAST();
    }

    @Override
    public Component acb() {
        return Type.DURATION_COOLDOWN_STATE.accept(COOLDOWN_REMAIN(), DURATION_REMAIN(), available());
    }

    public void stop() {
        DURATION_END();
    }
}
