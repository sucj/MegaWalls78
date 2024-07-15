package icu.suc.megawalls78.identity.trait;

import net.kyori.adventure.text.Component;

public abstract class Gathering extends Trait {

    private final Class<? extends Passive> internalPassive;

    public Gathering(String id, Class<? extends Passive> internalPassive) {
        super(id, Component.translatable("mw78.gathering." + id));
        this.internalPassive = internalPassive;
    }

    public Class<? extends Passive> getInternalPassive() {
        return internalPassive;
    }
}
