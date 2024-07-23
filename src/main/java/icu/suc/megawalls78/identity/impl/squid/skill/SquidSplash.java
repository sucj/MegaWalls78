package icu.suc.megawalls78.identity.impl.squid.skill;

import com.google.common.util.concurrent.AtomicDouble;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public class SquidSplash extends Skill {

    private static final double RANGE = 5.25D;
    private static final double DAMAGE = 3.5D;
    private static final double SCALE = 0.7D;
    private static final double MAX = 7.0D;

    public SquidSplash() {
        super("squid_splash", 100, 2000L);
    }

    @Override
    protected boolean use0(Player player) {
        Location location = player.getLocation();
        AtomicInteger count = new AtomicInteger();
        AtomicDouble heal = new AtomicDouble();
        EntityUtil.getNearbyEntities(player, RANGE).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !(entity instanceof Wither))
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    ((LivingEntity) entity).damage(DAMAGE, player);
                    entity.setVelocity(vector(location, entity));
                    count.getAndIncrement();
                    heal.getAndAdd(DAMAGE * SCALE);
                });
        int i = count.get();
        if (i == 0) {
            return noTarget(player);
        } else {
            player.heal(Math.min(heal.get(), MAX));
            playSkillEffects(player);
        }
        return summaryHit(player, i);
    }

    private void playSkillEffects(Player player) {
        ParticleUtil.playContractingCircleParticle(player.getLocation(), Particle.SPLASH, 64, RANGE, 525L);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    private static Vector vector(Location to, Entity entity) {
        double distance = to.distance(entity.getLocation());
        Vector vector = to.toVector().subtract(entity.getLocation().toVector()).normalize();
        vector.multiply(Math.min(distance / RANGE * 2, 1.0D));
        double y = vector.getY();
        if (y > 0) {
            vector.setY(Math.min(y, 0.2D));
        } else {
            vector.setY(Math.max(y, -0.2D));
        }
        vector.add(entity.getVelocity());
        return vector;
    }

    private static Vector NuggetMC_Vec(Location to, Entity entity) {
        Vector vector = to.toVector().subtract(entity.getLocation().toVector());
        double y = vector.getY();
        if (Math.abs(y) > 0.4D) {
            vector.setY(0.4D / y);
        }
        double length = vector.length();
        if (length > 0.9D) {
            vector.multiply(0.9D / length);
        }
        vector.add(entity.getVelocity());
        return vector;
    }
}
