package icu.suc.mw78.identity.regular.herobrine.passive;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.identity.trait.passive.DurationPassive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class Power extends DurationPassive {

    private static final double SCALE = 1.85D;

    public Power() {
        super("power", 6000L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        if (PASSIVE(event.getPlayer())) {
            DURATION_RESET();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && DURATION() && condition(event)) {
            power(event);
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return EntityUtil.isMeleeAttack(event) || EntityUtil.isSweepAttack(event);
    }

    private static void power(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * SCALE);
    }

    @Override
    public void unregister() {
        DURATION_END();
    }
}
