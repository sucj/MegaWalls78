package icu.suc.megawalls78.identity.impl.herebrine.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Power extends Passive implements IActionbar {

    private static final long DURATION = 6000L;
    private static final double SCALE = 1.85D;

    private long lastMills;

    public Power() {
        super("power");
    }

    @Override
    public Component acbValue() {
        return Type.DURATION.accept(System.currentTimeMillis(), lastMills, DURATION);
    }

    private boolean isActivate() {
        return System.currentTimeMillis() - lastMills <= DURATION;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player killer = EntityUtil.getKiller(event.getPlayer(), event.getDamageSource());
        if (shouldPassive(killer)) {
            lastMills = System.currentTimeMillis();
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getDamager() instanceof Player player) {
            if (shouldPassive(player) && isActivate() && (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) && player != this.getPlayer().getBukkitPlayer()) {
                event.setDamage(event.getDamage() * SCALE);
            }
        }
    }

    @Override
    public void unregister() {
        lastMills = 0;
    }
}
