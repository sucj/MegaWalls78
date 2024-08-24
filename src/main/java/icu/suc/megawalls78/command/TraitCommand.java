package icu.suc.megawalls78.command;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.management.TraitManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TraitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            player.openBook(TraitManager.book(MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity()));
            return true;
        }
        return false;
    }
}
