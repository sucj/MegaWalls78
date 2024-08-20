package icu.suc.megawalls78.util;

import icu.suc.megawalls78.MegaWalls78;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftFirework;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class ParticleUtil {

    public static void spawnFirework(Location location, FireworkEffect... effects) {
        EntityUtil.spawn(location, EntityUtil.Type.SAFE_FIREWORK, entity -> {
            Firework firework = (Firework) entity;
            FireworkMeta meta = firework.getFireworkMeta();
            meta.addEffects(effects);
            firework.setFireworkMeta(meta);
            firework.detonate();
        });
    }

    public static void spawnParticleOverhead(LivingEntity entity, Particle particle, int count) {
        Location location = entity.getEyeLocation().add(0.0D, 1.0D, 0.0D);
        World world = entity.getWorld();
        spawnParticle(world, particle, location, 1);
        for (int i = 0; i < count; i++) {
            spawnParticle(world, particle, location.clone().add(RandomUtil.RANDOM.nextDouble(0.5D), RandomUtil.RANDOM.nextDouble(0.5D), RandomUtil.RANDOM.nextDouble(0.5D)), 1);
        }
    }

    public static <T> void spawnParticleOverhead(LivingEntity entity, Particle particle, int count, T data) {
        Location location = entity.getEyeLocation().add(0.0D, 1.0D, 0.0D);
        World world = entity.getWorld();
        spawnParticle(world, particle, location, 1, data);
        for (int i = 0; i < count; i++) {
            spawnParticle(world, particle, location.clone().add(RandomUtil.RANDOM.nextDouble(0.5D), RandomUtil.RANDOM.nextDouble(0.5D), RandomUtil.RANDOM.nextDouble(0.5D)), 1, data);
        }
    }

    public static void spawnParticleRandomBody(Entity entity, Particle particle, int count) {
        World world = entity.getWorld();
        for (int i = 0; i < count; i++) {
            spawnParticle(world, particle, new Location(world, EntityUtil.getRandomBodyX(entity, 0.5D), EntityUtil.getRandomBodyY(entity) - 0.25D, EntityUtil.getRandomBodyZ(entity, 0.5D)), count, (RandomUtil.RANDOM.nextDouble() - 0.5) * 2.0, -RandomUtil.RANDOM.nextDouble(), (RandomUtil.RANDOM.nextDouble() - 0.5) * 2.0);
        }
    }

    public static void spawnParticleRandomBody(Entity entity, Particle particle, int count, double extra) {
        World world = entity.getWorld();
        for (int i = 0; i < count; i++) {
            spawnParticle(world, particle, new Location(world, EntityUtil.getRandomBodyX(entity, 0.5D), EntityUtil.getRandomBodyY(entity) - 0.25D, EntityUtil.getRandomBodyZ(entity, 0.5D)), count, (RandomUtil.RANDOM.nextDouble() - 0.5) * 2.0, -RandomUtil.RANDOM.nextDouble(), (RandomUtil.RANDOM.nextDouble() - 0.5) * 2.0, extra);
        }
    }

    public static <T> void spawnParticleRandomBody(Entity entity, Particle particle, int count, double extra, T data) {
        World world = entity.getWorld();
        for (int i = 0; i < count; i++) {
            spawnParticle(world, particle, new Location(world, EntityUtil.getRandomBodyX(entity, 0.5D), EntityUtil.getRandomBodyY(entity) - 0.25D, EntityUtil.getRandomBodyZ(entity, 0.5D)), count, (RandomUtil.RANDOM.nextDouble() - 0.5) * 2.0, -RandomUtil.RANDOM.nextDouble(), (RandomUtil.RANDOM.nextDouble() - 0.5) * 2.0, extra, data);
        }
    }

    public static <T> void spawnParticle(World world, Particle particle, Location location, int count, T data) {
        world.spawnParticle(particle, location, count, data);
    }

    public static void spawnParticle(World world, Particle particle, Location location, int count) {
        world.spawnParticle(particle, location, count);
    }

    public static void spawnParticle(World world, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ);
    }

    public static void spawnParticle(World world, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
    }

    public static <T> void spawnParticle(World world, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
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
                double radius = maxRadius * elapsedTime / duration;
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

    public static void playContractingCircleParticle(Location center, Particle particle, int count, double maxRadius, long duration) {
        playContractingCircleParticle(center, particle, count, maxRadius, duration, null);
    }

    public static <T> void playContractingCircleParticle(Location center, Particle particle, int count, double maxRadius, long duration, T data) {
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
                double radius = maxRadius * (1 - (double) elapsedTime / duration);
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
