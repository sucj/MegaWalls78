package icu.suc.mw78.identity.regular.spider.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.DurationPassive;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.mw78.identity.regular.spider.skill.Leap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait(value = "skitter", duration = 3000L)
public final class Skitter extends DurationPassive {

    private static final int ENERGY = 10;

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 60, 0);

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && DURATION()) {
            player.addPotionEffect(SPEED);
            PLAYER().increaseEnergy(ENERGY);
            summaryEffectSelf(player, SPEED);
            DURATION_END();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (PASSIVE(player)) {
            EntityUtil.removeMetadata(player, Leap.ID);
        }
    }

    @EventHandler
    public void onPLayerTickStart(ServerTickStartEvent event) {
        if (DURATION()) {
            return;
        }

        Player player = PLAYER().getBukkitPlayer();
        if (EntityUtil.getMetadata(player, Leap.ID)) {
            EntityUtil.removeMetadata(player, Leap.ID);
            DURATION_RESET();
        }
    }
}
