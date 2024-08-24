package icu.suc.mw78.identity.next.vindicator.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import org.bukkit.Tag;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait("door_breaker")
public final class DoorBreaker extends Passive {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 60, 2);

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && condition(event, player)) {
            potion(player);
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event, Player player) {
        return event.getEntity() instanceof HumanEntity human && human.isBlocking() && Tag.ITEMS_AXES.isTagged(player.getEquipment().getItemInMainHand().getType());
    }

    private void potion(Player player) {
        player.addPotionEffect(SPEED);
        summaryEffectSelf(player, SPEED);
    }
}
