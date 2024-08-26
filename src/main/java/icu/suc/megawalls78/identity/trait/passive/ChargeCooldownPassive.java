package icu.suc.megawalls78.identity.trait.passive;

import icu.suc.megawalls78.management.TraitManager;
import net.kyori.adventure.text.Component;

public abstract class ChargeCooldownPassive extends CooldownPassive {

    int CHARGE_COUNT;

    public ChargeCooldownPassive() {
        CHARGE_COUNT = CHARGE_GET();
    }

    protected int CHARGE_GET() {
        return TraitManager.charge(getClass());
    }

    protected boolean CHARGE() {
        return CHARGE(1) > CHARGE_GET();
    }

    protected int CHARGE(int delta) {
        return CHARGE_COUNT += delta;
    }

    protected int CHARGE_COUNT() {
        return CHARGE_COUNT;
    }

    protected void CHARGE_MAX() {
        CHARGE_COUNT = CHARGE_GET();
    }

    protected void CHARGE_RESET() {
        CHARGE_COUNT = 1;
    }

    @Override
    public Component acb() {
        return Type.CHARGE_COOLDOWN.accept(COOLDOWN_REMAIN(), CHARGE_COUNT(), CHARGE_GET());
    }
}
