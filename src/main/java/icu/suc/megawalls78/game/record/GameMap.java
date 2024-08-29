package icu.suc.megawalls78.game.record;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.List;

public record GameMap(String id, String version, List<String> authors, int size, Location[][] spawn, Location[][] wall, Location[][] region, Location spectator, Component name) {

    public GameMap(String id, String version, List<String> authors, int size, Location[][] spawn, Location[][] wall, Location[][] region, Location spectator) {
        this(id, version, authors, size, spawn, wall, region, spectator, Component.translatable("mw78.map." + id));
    }
}
