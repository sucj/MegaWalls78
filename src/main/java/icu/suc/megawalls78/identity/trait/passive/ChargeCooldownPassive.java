package icu.suc.megawalls78.identity.trait.passive;

import net.kyori.adventure.text.Component;

public abstract class ChargeCooldownPassive extends CooldownPassive {

    protected int CHARGE;

    int CHARGE_COUNT;

    public ChargeCooldownPassive(long cooldown, int charge) {
        super(cooldown);
        CHARGE = charge;
        CHARGE_COUNT = CHARGE;
    }

    protected boolean CHARGE() {
        return CHARGE(1) > CHARGE;
    }

    protected int CHARGE(int delta) {
        return CHARGE_COUNT += delta;
    }

    protected int CHARGE_COUNT() {
        return CHARGE_COUNT;
    }

    protected void CHARGE_MAX() {
        CHARGE_COUNT = CHARGE;
    }

    protected void CHARGE_RESET() {
        CHARGE_COUNT = 1;
    }

    @Override
    public Component acb() {
        return Type.CHARGE_COOLDOWN.accept(COOLDOWN_REMAIN(), CHARGE_COUNT(), CHARGE);
    }
}
