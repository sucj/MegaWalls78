package icu.suc.megawalls78.identity.impl.herobrine.passive;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.identity.trait.passive.DurationPassive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

    public final class Power extends DurationPassive {

    private static final double SCALE = 1.85D;

    public Power() {
        super("power", 6000L);
    }

    @EventHandler
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        if (event.isCancelled()) {
            return;
        }
        if (PASSIVE(event.getPlayer())) {
            DURATION_RESET();
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getDamager() instanceof Player player && PASSIVE(player) && DURATION() && condition(event)) {
            power(event);
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        return cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || cause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
    }

    private static void power(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * SCALE);
    }

    @Override
    public void unregister() {
        DURATION_END();
    }
}
