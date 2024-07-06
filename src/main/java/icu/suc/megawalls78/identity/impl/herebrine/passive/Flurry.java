package icu.suc.megawalls78.identity.impl.herebrine.passive;

import icu.suc.megawalls78.identity.trait.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Flurry extends Passive {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 60, 2);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 1);

    private int hit;

    public Flurry() {
        super("flurry", "Flurry");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getDamageSource().getCausingEntity() instanceof Player player) {
            if (shouldPassive(player) && !event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) && player != this.getPlayer().getBukkitPlayer()) {
                if (++hit >= 3) {
                    hit = 0;
                    player.addPotionEffect(SPEED);
                    player.addPotionEffect(REGENERATION);
                }
            }
        }
    }

    @Override
    public void unregister() {
        hit = 0;
    }
}
