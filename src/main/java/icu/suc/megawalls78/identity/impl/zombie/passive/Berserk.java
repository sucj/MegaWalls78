package icu.suc.megawalls78.identity.impl.zombie.passive;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Berserk extends Passive implements IActionbar {

    private static final long COOLDOWN = 15000L;

    private static final long DURATION = 6000L;
    private static final double SCALE = 1.75D;
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 120, 1);

    private long lastMills;
    private long duration;

    public Berserk() {
        super("berserk");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Arrow) {
            if (shouldPassive(player)) {
                long currentMillis = System.currentTimeMillis();
                if (currentMillis - lastMills >= COOLDOWN) {
                    lastMills = currentMillis;
                    duration = DURATION;
                    player.addPotionEffect(SPEED);
                }
            }
        } else if (event.getDamager() instanceof Player player) {
            if (shouldPassive(player) && duration > 0 && (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK))) {
                event.setDamage(event.getDamage() * SCALE);
            }
        }
    }

    @EventHandler
    public void onPlayerTickEnd(ServerTickEndEvent event) {
        if (duration > 0) {
            duration -= 50L;
        }
    }

    @Override
    public void unregister() {
        duration = 0;
    }

    @Override
    public Component acbValue() {
        return Type.COOLDOWN_DURATION.accept(System.currentTimeMillis(), lastMills, COOLDOWN, duration);
    }
}
