package icu.suc.mw78.identity.regular.dreadlord.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargeCooldownPassive;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Trait(value = "soul_eater", cooldown = 1000L, charge = 4)
public final class SoulEater extends ChargeCooldownPassive {

    private static final int FOOD = 3;
    private static final double HEAL = 2;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && condition(event) && CHARGE()) {
            heal(player);
            CHARGE_RESET();
            COOLDOWN_RESET();
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && (EntityUtil.isMeleeAttack(event) || EntityUtil.isArrowAttack(event));
    }

    private static void heal(Player player) {
        PlayerUtil.increaseFoodLevel(player, FOOD);
        player.heal(HEAL);
    }
}
