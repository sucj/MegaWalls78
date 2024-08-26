package icu.suc.mw78.identity.mythic.moleman.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait(value = "shortcut", charge = 3)
public final class Shortcut extends ChargePassive {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 80, 1);
    private static final PotionEffect HASTE = new PotionEffect(PotionEffectType.HASTE, 80, 1);

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (PASSIVE(player) && condition(event) && CHARGE()) {
            potion(player);
            CHARGE_RESET();
        }
    }

    private static boolean condition(BlockBreakEvent event) {
        return Tag.MINEABLE_SHOVEL.isTagged(event.getBlock().getType());
    }

    private void potion(Player player) {
        player.addPotionEffect(SPEED);
        player.addPotionEffect(HASTE);
        summaryEffectSelf(player, SPEED, HASTE);
    }
}
