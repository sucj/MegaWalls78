package icu.suc.megawalls78.identity.impl.zombie.passive;

import icu.suc.megawalls78.identity.trait.passive.DurationCooldownPassive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && condition_damage(event)) {
            DURATION_RESET();
            potion(player);
            EFFECT_SKILL.play(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && DURATION() && condition_attack(event)) {
            power(event);
        }
    }

    private static boolean condition_damage(EntityDamageByEntityEvent event) {
        return EntityUtil.isArrowAttack(event);
    }

    private static boolean condition_attack(EntityDamageByEntityEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        return cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || cause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
    }

    private void potion(Player player) {
        player.addPotionEffect(SPEED);
        summaryEffectSelf(player, SPEED);
    }

    private static void power(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * SCALE);
    }

    @Override
    public void unregister() {
        DURATION_END();
    }
}
