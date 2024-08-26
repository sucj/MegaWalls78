package icu.suc.megawalls78.identity.trait.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.management.TraitManager;
import net.kyori.adventure.text.Component;

public abstract class CooldownPassive extends TimerPassive implements IActionbar {

    long COOLDOWN_LAST;

    protected long COOLDOWN_GET() {
        return TraitManager.cooldown(getClass());
    }

    protected boolean COOLDOWN() {
        return CURRENT() - COOLDOWN_LAST() >= COOLDOWN_GET();
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

    protected void COOLDOWN_END() {
        COOLDOWN_LAST = 0;
    }

    protected long COOLDOWN_REMAIN() {
        return COOLDOWN_GET() - CURRENT() + COOLDOWN_LAST();
    }

    @Override
    public Component acb() {
        return Type.COOLDOWN.accept(COOLDOWN_REMAIN());
    }
}
