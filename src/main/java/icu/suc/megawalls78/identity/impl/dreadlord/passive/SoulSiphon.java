package icu.suc.megawalls78.identity.impl.dreadlord.passive;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.passive.DurationPassive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class SoulSiphon extends DurationPassive {

    private static final double SCALE = 1.85D;

    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 0);

    public SoulSiphon() {
        super("soul_siphon", 5000L);
    }

    @EventHandler
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        if (event.isCancelled()) {
            return;
        }
        GamePlayer gamePlayer = event.getPlayer();
        if (PASSIVE(gamePlayer)) {
            Player player = gamePlayer.getBukkitPlayer();
            if (player != null) {
                player.addPotionEffect(REGENERATION);
            }
            DURATION_RESET();
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getDamager() instanceof Player player && PASSIVE(player) && DURATION()) {
            power(event);
        }
    }

    private static void power(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * SCALE);
    }

    @Override
    public void unregister() {
        DURATION_END();
    }
}
