package icu.suc.megawalls78.identity.impl.zombie.passive;

import icu.suc.megawalls78.identity.trait.passive.DurationCooldownPassive;
import icu.suc.megawalls78.util.Effect;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Berserk extends DurationCooldownPassive {

    private static final double SCALE = 1.75D;

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 120, 1);

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ZOMBIE_HURT, SoundCategory.PLAYERS, 1.0F, 1.0F));

    public Berserk() {
        super("berserk", 15000L, 6000L);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && condition_damage(event)) {
            COOLDOWN_RESET();
            DURATION_RESET();
            potion(player);
            EFFECT_SKILL.play(player);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && DURATION() && condition_attack(event)) {
            power(event);
        }
    }

    private static boolean condition_damage(EntityDamageByEntityEvent event) {
        return event.getDamager() instanceof Arrow;
    }

    private static boolean condition_attack(EntityDamageByEntityEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        return cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || cause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
    }

    private static void potion(Player player) {
        player.addPotionEffect(SPEED);
    }

    private static void power(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * SCALE);
    }

    @Override
    public void unregister() {
        DURATION_END();
    }
}
