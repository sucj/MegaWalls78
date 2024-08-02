package icu.suc.megawalls78.identity.impl.zombie.skill;

import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class CircleOfHealing extends Skill {

    private static final double RADIUS = 5.0D;
    private static final double SELF = 8.0D;
    private static final double OTHER = 5.0D;

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> {
        ParticleUtil.playExpandingCircleParticle(player.getLocation(), Particle.ENTITY_EFFECT, 64, RADIUS, 500L, Color.GREEN);
        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.PLAYERS, 1.0F, 1.0F);
    });
    private static final Effect<LivingEntity> EFFECT_HEAD = Effect.create(entity -> ParticleUtil.spawnParticleOverhead(entity, Particle.HEART, (int) (OTHER / 2)));

    public CircleOfHealing() {
        super("circle_of_healing", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        AtomicInteger count = new AtomicInteger();
        heal(player);
        count.incrementAndGet();

        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> isValidAllies(player, entity))
                .forEach(entity -> {
                    heal((Player) entity);
                    summaryHealBy(player, (Player) entity);
                    count.getAndIncrement();
                });

        EFFECT_SKILL.play(player);

        return summaryHeal(player, count.get());
    }

    private static void heal(Player entity) {
        entity.heal(SELF);
        EFFECT_HEAD.play(entity);
    }
}
