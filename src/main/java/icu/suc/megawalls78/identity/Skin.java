package icu.suc.megawalls78.identity;

import net.kyori.adventure.text.Component;

public record Skin(String id, String value, String signature) {
    public Component name() {
        return Component.translatable("mw78.skin." + id);
    }
}
