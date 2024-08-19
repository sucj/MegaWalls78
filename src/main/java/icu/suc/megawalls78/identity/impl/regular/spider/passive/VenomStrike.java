package icu.suc.megawalls78.identity.impl.regular.spider.passive;

import icu.suc.megawalls78.identity.trait.passive.ChargeCooldownPassive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class VenomStrike extends ChargeCooldownPassive {

    private static final PotionEffect POISON = new PotionEffect(PotionEffectType.POISON, 100, 0);

    public VenomStrike() {
        super("venom_strike", 7000L, 4);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && COOLDOWN() && condition(event) && CHARGE()) {
            potion((Player) event.getEntity(), player);
            CHARGE_RESET();
            COOLDOWN_RESET();
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && EntityUtil.isMeleeAttack(event);
    }

    private void potion(Player target, Player source) {
        EntityUtil.addPotionEffect(target, POISON, source);
        summaryEffectOther(source, target, POISON);
    }
}
