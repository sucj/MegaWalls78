package icu.suc.megawalls78.identity.trait;

import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.management.TraitManager;

public abstract class Gathering extends Trait {

    private Passive passive;

    public Class<? extends Passive> getInternal() {
        return TraitManager.internal(getClass());
    }

    public Passive getPassive() {
        return passive;
    }

    public void setPassive(Passive passive) {
        this.passive = passive;
    }
}
