package icu.suc.megawalls78.util;

import icu.suc.megawalls78.MegaWalls78;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleUtil {

    public static void spawnParticleOverhead(LivingEntity entity, Particle particle, int count) {
        Location location = entity.getEyeLocation().add(0.0D, 1.0D, 0.0D);
        World world = entity.getWorld();
        spawnParticle(world, particle, location, 1);
        for (int i = 0; i < count; i++) {
            spawnParticle(world, particle, location.clone().add(RandomUtil.RANDOM.nextDouble(0.5D), RandomUtil.RANDOM.nextDouble(0.5D), RandomUtil.RANDOM.nextDouble(0.5D)), 1);
        }
    }

    public static void spawnParticleRandomBody(Entity entity, Particle particle, int count) {
        World world = entity.getWorld();
        for (int i = 0; i < count; i++) {
            spawnParticle(world, particle, new Location(world, getParticleX(entity, 0.5D), getRandomBodyY(entity) - 0.25D, getParticleZ(entity, 0.5D)), count, (RandomUtil.RANDOM.nextDouble() - 0.5) * 2.0, -RandomUtil.RANDOM.nextDouble(), (RandomUtil.RANDOM.nextDouble() - 0.5) * 2.0);
        }
    }

    private static double getParticleX(Entity entity, double widthScale) {
        return entity.getX() + entity.getWidth() * (2.0 * RandomUtil.RANDOM.nextDouble() - 1.0) * widthScale;
    }

    private static double getRandomBodyY(Entity entity) {
        return entity.getY() + entity.getHeight() * RandomUtil.RANDOM.nextDouble();
    }

    private static double getParticleZ(Entity entity, double widthScale) {
        return entity.getZ() + entity.getWidth() * (2.0 * RandomUtil.RANDOM.nextDouble() - 1.0) * widthScale;
    }

    public static void spawnParticle(World world, Particle particle, Location location, int count) {
        world.spawnParticle(particle, location, count);
    }

    public static void spawnParticle(World world, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ);
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
