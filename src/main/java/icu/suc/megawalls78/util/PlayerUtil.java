package icu.suc.megawalls78.util;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.management.GameManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerUtil {

    public static boolean isValidAllies(Player origin, Entity entity) {
        if (entity instanceof Player target) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            GameTeam team = gameManager.getPlayer(origin).getTeam();
            GamePlayer gamePlayer = gameManager.getPlayer(target);
            return gamePlayer != null && !gameManager.isSpectator(target) && gamePlayer.getTeam().equals(team);
        }
        return false;
    }

    public static Identity getIdentity(Player player) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        return gameManager.getPlayer(player).getIdentity();
    }

    public static void setStarvation(Player player, float saturationLevel) {
        ((CraftPlayer) player).getHandle().getFoodData().setSaturation(saturationLevel);
    }

    public static BlockFace getFacingTowardsPlayer(Block block, Player player) {
        double x = player.getLocation().getX() - block.getX();
        double z = player.getLocation().getZ() - block.getZ();
        if (Math.abs(x) > Math.abs(z)) {
            return x > 0 ? BlockFace.EAST : BlockFace.WEST;
        } else {
            return z > 0 ? BlockFace.SOUTH : BlockFace.WEST;
        }
    }
}
