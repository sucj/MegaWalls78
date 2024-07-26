package icu.suc.megawalls78.command;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.RandomUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SurfaceCommand implements CommandExecutor {
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
            if (gameManager.getRunner().isDm()) {
                return true;
            }
            if (gameManager.getState().equals(GameState.OPENING) || gameManager.getState().equals(GameState.PREPARING)) {
                player.teleport(RandomUtil.getRandomSpawn(gameManager.getPlayer(player).getTeam().spawn()));
            }
        }
        return true;
    }
}
