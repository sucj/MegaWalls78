package icu.suc.megawalls78.game.record;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public record GameMap(String id, Location[][] spawn, Location[][] wall, Location[][] region, Location spectator) {

    public Component name() {
        return Component.translatable("mw78.map." + id);
    }
}
