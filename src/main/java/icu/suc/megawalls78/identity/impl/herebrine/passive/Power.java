package icu.suc.megawalls78.identity.impl.herebrine.passive;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class Power extends Passive implements IActionbar {

    private static final long DURATION = 6000L;
    private static final double SCALE = 1.85D;

    private long lastMills;

    public Power() {
        super("power");
    }

    @Override
    public Component acb() {
        return Type.DURATION.accept(System.currentTimeMillis(), lastMills, DURATION);
    }

    @EventHandler
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        if (event.isCancelled()) {
            return;
        }
        if (shouldPassive(event.getUuid())) {
            lastMills = System.currentTimeMillis();
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getDamager() instanceof Player player) {
            if (shouldPassive(player) && System.currentTimeMillis() - lastMills <= DURATION && (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK))) {
                event.setDamage(event.getDamage() * SCALE);
            }
        }
    }

    @Override
    public void unregister() {
        lastMills = 0;
    }
}
