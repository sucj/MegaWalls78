package icu.suc.megawalls78.identity.trait.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import net.kyori.adventure.text.Component;

public abstract class ChargePassive extends Passive implements IActionbar {

    protected int CHARGE;

    int CHARGE_COUNT;

    public ChargePassive(int charge) {
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
        return Type.CHARGE.accept(CHARGE_COUNT(), CHARGE);
    }
}
