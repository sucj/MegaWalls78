package icu.suc.megawalls78.game.record;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.List;

public record GameMap(String id, String version, List<String> authors, int size, Location[][] spawn, Location[][] wall, Location[][] region, Location spectator) {

    public Component name() {
        return Component.translatable("mw78.map." + id);
    }
}
