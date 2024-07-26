package icu.suc.megawalls78.identity.impl.herobrine.passive;

import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Flurry extends ChargePassive {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 60, 1);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 0);

    public Flurry() {
        super("flurry", 3);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && condition(event) && CHARGE()) {
            potion(player);
            CHARGE_RESET();
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && !event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
    }

    private static void potion(Player player) {
        player.addPotionEffect(SPEED);
        player.addPotionEffect(REGENERATION);
    }

    @Override
    public void unregister() {
        CHARGE_MAX();
    }
}
