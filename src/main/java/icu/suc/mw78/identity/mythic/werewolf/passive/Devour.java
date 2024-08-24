package icu.suc.mw78.identity.mythic.werewolf.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

@Trait("devour")
public class Devour extends Passive {

    private static final double RADIUS = 5.0D;
    private static final int HUNGER = 3;

    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 50, 1);

    @EventHandler(ignoreCancelled = true)
    public void onEatSteak(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (PASSIVE(player) && condition(event)) {
            potion(player);
            EntityUtil.getNearbyEntities(player, RADIUS).stream()
                    .filter(entity -> entity instanceof Player)
                    .filter(entity -> !isValidAllies(player, entity))
                    .forEach(entity -> hunger((Player) entity));
        }
    }

    private static boolean condition(PlayerItemConsumeEvent event) {
        return event.getItem().getType().equals(Material.COOKED_BEEF);
    }

    private void potion(Player player) {
        player.addPotionEffect(REGENERATION);
        summaryEffectSelf(player, REGENERATION);
    }

    private static void hunger(Player player) {
        PlayerUtil.decreaseFoodLevel(player, HUNGER);
    }
}
