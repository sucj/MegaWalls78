package icu.suc.megawalls78.identity.impl.zombie.skill;

import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class CircleOfHealing extends Skill {

    private static final double RANGE = 5.0D;
    private static final double SELF = 8.0D;
    private static final double OTHER = 5.0D;

    public CircleOfHealing() {
        super("circle_of_healing", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        ParticleUtil.playExpandingCircleParticle(player.getLocation(), Particle.ENTITY_EFFECT, 64, RANGE, 500L, Color.GREEN);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.PLAYERS, 1.0F, 1.0F);

        AtomicInteger count = new AtomicInteger();
        player.heal(SELF);
        ParticleUtil.spawnParticleOverhead(player, Particle.HEART, (int) (SELF / 2));
        count.incrementAndGet();

        EntityUtil.getNearbyEntities(player, RANGE).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> isValidAllies(player, entity))
                .forEach(entity -> {
                    ((Player) entity).heal(OTHER);
                    ParticleUtil.spawnParticleOverhead((Player) entity, Particle.HEART, (int) (OTHER / 2));
                    count.getAndIncrement();
                });
        return summaryHeal(player, count.get());
    }
}
