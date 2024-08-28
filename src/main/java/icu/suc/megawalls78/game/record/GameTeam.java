package icu.suc.megawalls78.game.record;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

public record GameTeam(String id, NamedTextColor color, Location[][] palace, Location[][] spawn, Location[][] region, Location wither, Component name, Component abbr, Component chat) {

    public static final GameTeam SPEC = of("spec", NamedTextColor.GRAY, null, null, null, null);

    public static GameTeam of(String id, NamedTextColor color, Location[][] palace, Location[][] spawn, Location[][] region, Location wither) {
        return new GameTeam(id, color, palace, spawn, region, wither, Component.translatable("mw78.team." + id + ".name"), Component.translatable("mw78.team." + id + ".abbr"), Component.translatable("mw78.team." + id + ".chat"));
    }
}
