package icu.suc.megawalls78.identity;

import net.kyori.adventure.text.Component;

public record Skin(String id, String value, String signature, Component name) {

    public Skin(String id, String value, String signature) {
        this(id, value, signature, Component.translatable("mw78.skin." + id));
    }
}
