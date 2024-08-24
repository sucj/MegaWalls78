package icu.suc.megawalls78.identity.trait.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import net.kyori.adventure.text.Component;

public abstract class DurationPassive extends TimerPassive implements IActionbar {

    protected long DURATION;

    long DURATION_LAST;

    public DurationPassive(long duration) {
        DURATION = duration;
    }

    protected boolean DURATION() {
        return CURRENT() - DURATION_LAST() <= DURATION;
    }

    protected long DURATION(long delta) {
        return DURATION_LAST -= delta;
    }

    protected long DURATION_LAST() {
        return DURATION_LAST;
    }

    protected void DURATION_RESET() {
        DURATION_LAST = CURRENT();
    }

    protected void DURATION_END() {
        DURATION_LAST = 0;
    }

    @Override
    public Component acb() {
        return Type.DURATION.accept(DURATION - CURRENT() + DURATION_LAST());
    }
}
