package icu.suc.megawalls78.identity.impl.golem.passive;

import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.DamageSource;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class IronConstitution extends Passive {

    private static final double SCALE = 0.8D;

    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 180, 0);

    public IronConstitution() {
        super("iron_constitution");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player) && condition_true(event)) {
            event.setDamage(event.getFinalDamage() * SCALE);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player) && condition_projectile(event)) {
            player.addPotionEffect(RESISTANCE);
        }
    }

    public static boolean condition_true(EntityDamageEvent event) {
        return DamageSource.isTrueDamage(event.getDamageSource().getDamageType());
    }

    public static boolean condition_projectile(EntityDamageByEntityEvent event) {
        return EntityUtil.isArrowAttack(event) || event.getDamager() instanceof Snowball;
    }
}
