package icu.suc.mw78.identity.regular.shaman.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait(value = "heroism", charge = 6)
public final class Heroism extends ChargePassive {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 100, 1);
    private static final PotionEffect WEAKNESS = new PotionEffect(PotionEffectType.WEAKNESS, 100, 0);

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && condition(event) && CHARGE()) {
            player.addPotionEffect(SPEED);
            summaryEffectSelf(player, SPEED);
            Player target = (Player) event.getEntity();
            target.addPotionEffect(WEAKNESS);
            summaryEffectOther(player, target, WEAKNESS);
            CHARGE_RESET();
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && (EntityUtil.isMeleeAttack(event) || event.getDamageSource().getDamageType().equals(DamageType.WIND_CHARGE));
    }
}
