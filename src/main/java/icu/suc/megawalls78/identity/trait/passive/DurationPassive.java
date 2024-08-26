package icu.suc.megawalls78.identity.trait.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.management.TraitManager;
import net.kyori.adventure.text.Component;

public abstract class DurationPassive extends TimerPassive implements IActionbar {

    long DURATION_LAST;

    protected long DURATION_GET() {
        return TraitManager.duration(getClass());
    }

    protected boolean DURATION() {
        return CURRENT() - DURATION_LAST() <= DURATION_GET();
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
        return Type.DURATION.accept(DURATION_GET() - CURRENT() + DURATION_LAST());
    }
}
