package icu.suc.megawalls78.util;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.management.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerUtil {

    public static boolean isValidAllies(Player origin, Entity entity) {
        if (entity instanceof Player target) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            GameTeam team = gameManager.getPlayer(origin).getTeam();
            GamePlayer gamePlayer = gameManager.getPlayer(target);
            return gamePlayer != null && !gameManager.isSpectator(target) && gamePlayer.getTeam().equals(team);
        }
        return ((CraftPlayer) origin).getHandle().getTeam() == ((CraftEntity) entity).getHandle().getTeam();
    }

    public static Identity getIdentity(Player player) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        return gameManager.getPlayer(player).getIdentity();
    }

    public static void setStarvation(Player player, float saturationLevel) {
        ((CraftPlayer) player).getHandle().getFoodData().setSaturation(saturationLevel);
    }

    public static ItemStack getPlayerMainHand(Player player) {
        return player.getEquipment().getItemInMainHand();
    }

    public static UUID getKiller(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Player killer = player.getKiller();
        UUID uuid = null;
        if (killer == null) {
            if (event.deathMessage() instanceof TranslatableComponent deathMessage) {
                List<TranslationArgument> arguments = deathMessage.arguments();
                List<UUID> uuids = Lists.newArrayList();
                for (TranslationArgument argument : arguments) {
                    if (argument.value() instanceof Component component) {
                        HoverEvent<?> hoverEvent = component.hoverEvent();
                        if (hoverEvent != null && hoverEvent.value() instanceof HoverEvent.ShowEntity showEntity) {
                            uuids.add(showEntity.id());
                        }
                    }
                }
                if (!uuids.isEmpty()) {
                    uuid = uuids.getLast();
                }
            }
        } else {
            uuid = killer.getUniqueId();
        }
        if (player.getUniqueId().equals(uuid)) {
            uuid = null;
        }
        return uuid;
    }

    public static void addFoodLevel(Player player, int level) {
        int food = player.getFoodLevel();
        player.setFoodLevel(Math.min(food + level, 20));
    }
}
