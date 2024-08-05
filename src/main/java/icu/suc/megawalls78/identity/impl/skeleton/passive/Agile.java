package icu.suc.megawalls78.identity.impl.skeleton.passive;

import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Agile extends CooldownPassive {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 140, 1);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 140, 0);

    public Agile() {
        super("agile", 12000L);
    }

    @EventHandler
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && condition(event)) {
            if (COOLDOWN()) {
                player.addPotionEffect(SPEED);
                COOLDOWN_RESET();
            }
            player.addPotionEffect(REGENERATION);
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && EntityUtil.isArrowAttack(event);
    }
}
