package icu.suc.megawalls78.identity.impl.dreadlord.passive;

import icu.suc.megawalls78.identity.trait.passive.ChargeCooldownPassive;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class SoulEater extends ChargeCooldownPassive {

    private static final int FOOD = 3;
    private static final double HEAL = 2;

    public SoulEater() {
        super("soul_eater", 1000L, 4);
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && condition(event) && CHARGE()) {
            heal(player);
            CHARGE_RESET();
            COOLDOWN_RESET();
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && (EntityUtil.isMeleeAttack(event) || event.getDamager() instanceof AbstractArrow);
    }

    private static void heal(Player player) {
        PlayerUtil.addFoodLevel(player, FOOD);
        player.heal(HEAL);
    }
}
