package icu.suc.megawalls78.command;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.management.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EnergyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            if (strings.length == 1) {
                GameManager gameManager = MegaWalls78.getInstance().getGameManager();
                if (gameManager.isSpectator(player)) {
                    return true;
                }
                if (gameManager.inFighting()) {
                    GamePlayer gamePlayer = gameManager.getPlayer(player);
                    gamePlayer.setEnergy(Integer.parseInt(strings[0]));
                }
                return true;
            } else if (strings.length == 0) {
                GameManager gameManager = MegaWalls78.getInstance().getGameManager();
                if (gameManager.isSpectator(player)) {
                    return true;
                }
                if (gameManager.inFighting()) {
                    GamePlayer gamePlayer = gameManager.getPlayer(player);
                    gamePlayer.setEnergy(gamePlayer.getIdentity().getEnergy());
                }
                return true;
            }
        }
        return false;
    }
}
