package icu.suc.megawalls78.identity.trait.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import net.kyori.adventure.text.Component;

public abstract class CooldownPassive extends TimerPassive implements IActionbar {

    protected long COOLDOWN;

    long COOLDOWN_LAST;

    public CooldownPassive(String id, long cooldown) {
        super(id);
        COOLDOWN = cooldown;
    }

    protected boolean COOLDOWN() {
        return CURRENT() - COOLDOWN_LAST() >= COOLDOWN;
    }

    protected long COOLDOWN(long delta) {
        return COOLDOWN_LAST += delta;
    }

    protected long COOLDOWN_LAST() {
        return COOLDOWN_LAST;
    }

    protected void COOLDOWN_RESET() {
        COOLDOWN_LAST = CURRENT();
    }

    protected long COOLDOWN_REMAIN() {
        return COOLDOWN - CURRENT() + COOLDOWN_LAST();
    }

    @Override
    public Component acb() {
        return Type.COOLDOWN.accept(COOLDOWN_REMAIN());
    }
}
