package icu.suc.mw78.identity.regular.blaze.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargeCooldownPassive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait("melting_core")
public final class MeltingCore extends ChargeCooldownPassive {

    private static final int DURATION = 60;

    private static final PotionEffect REGENERATION_1 = new PotionEffect(PotionEffectType.REGENERATION, DURATION, 0);
    private static final PotionEffect REGENERATION_2 = new PotionEffect(PotionEffectType.REGENERATION, DURATION, 1);

    public MeltingCore() {
        super(4000L, 8);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMelee(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && condition_melee(event) && CHARGE()) {
            handle(player, event, REGENERATION_1);
            CHARGE_RESET();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && condition_shoot(event)) {
            if (CHARGE()) {
                handle(player, event, REGENERATION_2);
                CHARGE_RESET();
            } else if (CHARGE()) {
                handle(player, event, REGENERATION_2);
                CHARGE_RESET();
            }
        }
    }

    private static boolean condition_melee(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && EntityUtil.isMeleeAttack(event);
    }

    private static boolean condition_shoot(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && EntityUtil.isArrowAttack(event);
    }

    private void handle(Player player, EntityDamageByEntityEvent event, PotionEffect effect) {
        player.addPotionEffect(effect);
        summaryEffectSelf(player, effect);
        event.getEntity().setFireTicks(DURATION);
    }
}
