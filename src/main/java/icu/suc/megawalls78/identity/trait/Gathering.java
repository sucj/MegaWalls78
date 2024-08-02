package icu.suc.megawalls78.identity.trait;

import icu.suc.megawalls78.identity.trait.passive.Passive;
import net.kyori.adventure.text.Component;

public abstract class Gathering extends Trait {

    private final Class<? extends Passive> internal;
    private Passive passive;

    public Gathering(String id) {
        this(id, null);
    }

    public Gathering(String id, Class<? extends Passive> internal) {
        super(id, Component.translatable("mw78.gathering." + id));
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
