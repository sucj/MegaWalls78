package icu.suc.megawalls78.identity.trait.passive;

import net.kyori.adventure.text.Component;

public class DurationCooldownPassive extends CooldownPassive {

    protected long DURATION;

    long DURATION_LAST;

    public DurationCooldownPassive(String id, long cooldown, long duration) {
        super(id, cooldown);
        DURATION = duration;
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
        COOLDOWN(-DURATION_REMAIN());
        DURATION_LAST = 0;
    }

    protected long DURATION_REMAIN() {
        return DURATION - CURRENT() + DURATION_LAST();
    }

    @Override
    public Component acb() {
        return Type.DURATION_COOLDOWN.accept(COOLDOWN_REMAIN(), DURATION_REMAIN());
    }
}
