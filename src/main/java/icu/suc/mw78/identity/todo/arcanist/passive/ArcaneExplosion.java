package icu.suc.mw78.identity.todo.arcanist.passive;

import icu.suc.megawalls78.identity.trait.passive.ChargeCooldownPassive;
import icu.suc.megawalls78.util.*;
import icu.suc.megawalls78.util.Effect;
import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class ArcaneExplosion extends ChargeCooldownPassive {

    private static final double RADIUS = 5.0D;
    private static final double DAMAGE = 2.0D;

    private static final Effect<Location> EFFECT_SKILL = Effect.create(location -> {
        ParticleUtil.spawnParticle(location.getWorld(), Particle.EXPLOSION, location, 1);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 1.0F, 1.0F);
    });

    public ArcaneExplosion() {
        super("arcane_explosion", 1000L, 5);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && condition(event) && CHARGE()) {
            CHARGE_RESET();
            AtomicInteger count = new AtomicInteger();
            EntityUtil.getNearbyEntities(player, RADIUS).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> !(entity instanceof Wither))
                    .filter(entity -> !isValidAllies(player, entity))
                    .forEach(entity -> {
                        ((LivingEntity) entity).damage(DAMAGE, DamageSource.of(DamageType.PLAYER_EXPLOSION, player));
                        count.getAndIncrement();
                    });
            EFFECT_SKILL.play(player.getLocation());
            summaryHit(player, count.get());
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return EntityUtil.isMeleeAttack(event) || EntityUtil.isSweepAttack(event) || EntityUtil.isArrowAttack(event);
    }
}
