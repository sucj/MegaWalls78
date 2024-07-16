package icu.suc.megawalls78.util;

import icu.suc.megawalls78.MegaWalls78;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleUtil {

    public static void spawnParticleOverhead(LivingEntity entity, Particle particle, int count) {
        Location location = entity.getEyeLocation().add(0.0D, 1.0D, 0.0D);
        World world = entity.getWorld();
        world.spawnParticle(particle, location, 1);
        for (int i = 1; i < count; i++) {
            world.spawnParticle(particle, location.clone().add(RandomUtil.RANDOM.nextDouble(0.5D), RandomUtil.RANDOM.nextDouble(0.5D), RandomUtil.RANDOM.nextDouble(0.5D)), 1);
        }
    }

    public static void playExpandingCircleParticle(Location center, Particle particle, int count, double maxRadius, long duration) {
        playExpandingCircleParticle(center, particle, count, maxRadius, duration, null);
    }

    public static <T> void playExpandingCircleParticle(Location center, Particle particle, int count, double maxRadius, long duration, T data) {
        new BukkitRunnable() {
            private final double angleCount = (2 * Math.PI) / count;
            private final long startTime = System.currentTimeMillis();
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > duration) {
                    this.cancel();
                    return;
                }
                double radius = (maxRadius * elapsedTime) / duration;
                for (int i = 0; i < count; i++) {
                    double angle = i * angleCount;
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);
                    Location location = new Location(center.getWorld(), x, center.getY(), z);
                    center.getWorld().spawnParticle(particle, location, 1, 0, 0, 0, 0, data);
                }
            }
        }.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
    }
}
