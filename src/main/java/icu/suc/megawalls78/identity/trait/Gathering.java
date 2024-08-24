package icu.suc.megawalls78.identity.trait;

import icu.suc.megawalls78.identity.trait.passive.Passive;

public abstract class Gathering extends Trait {

    private final Class<? extends Passive> internal;
    private Passive passive;

    public Gathering() {
        this(null);
    }

    public Gathering(Class<? extends Passive> internal) {
        this.internal = internal;
    }

    public Class<? extends Passive> getInternal() {
        return internal;
    }

    public Passive getPassive() {
        return passive;
    }

    public void setPassive(Passive passive) {
        this.passive = passive;
    }
}
