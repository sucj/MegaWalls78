package icu.suc.megawalls78.command;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.management.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamchestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            if (gameManager.inWaiting()) {
                return true;
            }
            if (gameManager.isSpectator(player)) {
                return true;
            }
            player.openInventory(gameManager.teamChest(gameManager.getPlayer(player).getTeam()));
            return true;
        }
        return false;
    }
}
