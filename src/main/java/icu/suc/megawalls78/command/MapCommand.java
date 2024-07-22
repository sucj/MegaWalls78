package icu.suc.megawalls78.command;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.record.GameMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        GameMap map = MegaWalls78.getInstance().getGameManager().getMap();
        commandSender.sendMessage(map.toString());
        return true;
    }
}
