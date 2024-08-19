package icu.suc.mw78.identity.next.vindicator.passive;

import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.Effect;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public final class Tetanus extends ChargePassive {

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_VINDICATOR_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F));

    public Tetanus() {
        super("tetanus", 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && condition(event) && CHARGE()) {
            handle(event);
            EFFECT_SKILL.play(player);
            CHARGE_RESET();
        }
    }

    public static boolean condition(EntityDamageByEntityEvent event) {
        return event.isApplicable(EntityDamageEvent.DamageModifier.ABSORPTION) && event.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION) < 0;
    }

    public static void handle(EntityDamageByEntityEvent event) {
        event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, event.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION) / 2);
    }
}
