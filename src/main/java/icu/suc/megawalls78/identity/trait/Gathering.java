package icu.suc.megawalls78.identity.trait;

import net.kyori.adventure.text.Component;

public abstract class Gathering extends Trait {

    private final Class<? extends Passive> internal;

    public Gathering(String id, Class<? extends Passive> internal) {
        super(id, Component.translatable("mw78.gathering." + id));
        this.internal = internal;
    }

    public Class<? extends Passive> getInternal() {
        return internal;
    }
}
