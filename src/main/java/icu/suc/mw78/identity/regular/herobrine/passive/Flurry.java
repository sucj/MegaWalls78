package icu.suc.mw78.identity.regular.herobrine.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait("flurry")
public final class Flurry extends ChargePassive {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 60, 1);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 0);

    public Flurry() {
        super(3);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && condition(event) && CHARGE()) {
            potion(player);
            CHARGE_RESET();
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && (EntityUtil.isMeleeAttack(event) || EntityUtil.isArrowAttack(event));
    }

    private void potion(Player player) {
        player.addPotionEffect(SPEED);
        player.addPotionEffect(REGENERATION);
        summaryEffectSelf(player, SPEED, REGENERATION);
    }
}
