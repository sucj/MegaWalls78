package icu.suc.megawalls78.identity.trait.passive;

import net.kyori.adventure.text.Component;

public abstract class DurationCooldownPassive extends CooldownPassive {

    protected long DURATION;

    long DURATION_LAST;

    public DurationCooldownPassive(long cooldown, long duration) {
        super(cooldown);
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
        DURATION_RESET_ONLY();
        COOLDOWN(DURATION);
    }

    protected void DURATION_RESET_ONLY() {
        long current = CURRENT();
        COOLDOWN_LAST = current;
        DURATION_LAST = current;
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
        return Type.DURATION_COOLDOWN.accept(COOLDOWN_REMAIN(), DURATION_REMAIN());
    }
}
