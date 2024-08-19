package icu.suc.mw78.identity.mythic.werewolf.passive;

import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BloodLust extends ChargePassive {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 120, 0);
    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 120, 0);

    public BloodLust() {
        super("blood_lust", 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && condition_attack(event) && CHARGE()) {
            potion(player);
            CHARGE_RESET();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player)) {
            CHARGE_RESET();
        }
    }

    private static boolean condition_attack(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && (EntityUtil.isMeleeAttack(event) || EntityUtil.isArrowAttack(event));
    }

    private void potion(Player player) {
        player.addPotionEffect(SPEED);
        player.addPotionEffect(RESISTANCE);
        summaryEffectSelf(player, SPEED, RESISTANCE);
    }
}
