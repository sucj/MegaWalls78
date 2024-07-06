package icu.suc.megawalls78.game.record;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

public record GameTeam(String id, NamedTextColor color, Location[][] palace, Location[][] spawn, Location[][] region,
                       Location wither) {

    public Component name() {
        return Component.translatable("mw78.team." + id + ".name");
    }

    public Component abbr() {
        return Component.translatable("mw78.team." + id + ".abbr");
    }

    public Component chat() {
        return Component.translatable("mw78.team." + id + ".chat");
    }
}
