package icu.suc.mw78.identity.mythic.assassin.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.mw78.identity.mythic.assassin.skill.ShadowCloak;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Trait(value = "shadow_step", cooldown = 10000L)
public final class ShadowStep extends CooldownPassive {

    private static final double RADIUS = 25.0D; // 25格内的远程伤害

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() &&
                event.getDamageSource().getCausingEntity() instanceof Player damager &&
                player.isSneaking() &&
                player.getLocation().distance(damager.getLocation()) <= RADIUS &&
                !EntityUtil.getMetadata(player, ShadowCloak.ID)) {

            player.setFallDistance(0);
            player.teleport(EntityUtil.getBackwardLocation(damager, 0.8D));

            event.setCancelled(true);

            COOLDOWN_RESET();
        }
    }
}
