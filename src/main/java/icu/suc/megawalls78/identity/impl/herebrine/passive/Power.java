package icu.suc.megawalls78.identity.impl.herebrine.passive;

import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Power extends Passive {

  private long lastMills;

  public Power() {
    super("power", "Power");
  }

  private boolean isActive() {
    return System.currentTimeMillis() - lastMills <= 6000L;
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
      if (shouldPassive(player) && isActive() && (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) && player != this.getPlayer().getBukkitPlayer()) {
        event.setDamage(event.getDamage() * 1.85);
      }
    }
  }

  @Override
  public void unregister() {
    lastMills = 0;
  }
}
