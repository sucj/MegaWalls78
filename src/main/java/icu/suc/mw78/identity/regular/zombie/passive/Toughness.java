package icu.suc.mw78.identity.regular.zombie.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait("toughness")
public final class Toughness extends ChargePassive {

    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 20, 0);

    public Toughness() {
        super(3);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player) && condition(event) && CHARGE()) {
            potion(player);
            CHARGE_RESET();
        }
    }

    private static boolean condition(EntityDamageEvent event) {
        return event.getDamageSource().getCausingEntity() != null;
    }

    private void potion(Player player) {
        player.addPotionEffect(RESISTANCE);
        summaryEffectSelf(player, RESISTANCE);
    }
}
