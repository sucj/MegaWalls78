package icu.suc.megawalls78.identity.impl.spider.skill;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.impl.spider.passive.Skitter;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public final class Leap extends Skill {

    private static final double RANGE = 4.0D;
    private static final double BASE_DAMAGE = 3.0D;
    private static final double BONUS_DAMAGE = 1.0D;
    private static final double BONUS_DISTANCE = 4.0D;
    private static final double MAX_BONUS_DAMAGE = 5.0D;
    private static final double REDUCE_DAMAGE = 1.0D;
    private static final double REDUCE_DISTANCE = 2.0D;
    private static final PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOWNESS, 80, 0);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 1);
    private static final float SCALE = 0.5F;

    private Task task;

    public Leap() {
        super("leap", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        Location location = player.getEyeLocation();
        player.getWorld().playSound(location, Sound.ENTITY_SPIDER_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        boolean run = false; // For continuous jump
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        Vector vector = location.getDirection();
        Skitter.getMode(player.getUniqueId()).accept(vector);
        player.setVelocity(vector);

        if (run) {
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
        }

        return true;
    }

    private final class Task extends BukkitRunnable {

        private final Player player;

        private Location lastLocation;
        private double travelLength;

        private Task(Player player) {
            this.player = player;

            this.lastLocation = player.getEyeLocation();
        }

        @Override
        public void run() {
            if (player.isDead()) {
                this.cancel();
                return;
            }
            if (EntityUtil.isOnGround(player)) {
                updateTravelLength();
                player.getWorld().playSound(lastLocation, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.7F, 0.5F);
                player.addPotionEffect(REGENERATION);
                AtomicInteger count = new AtomicInteger();
                EntityUtil.getNearbyEntities(player, RANGE).stream()
                        .filter(entity -> entity instanceof LivingEntity)
                        .filter(entity -> !(entity instanceof Wither))
                        .filter(entity -> !PlayerUtil.isValidAllies(player, entity))
                        .forEach(entity -> {
                            ((LivingEntity) entity).addPotionEffect(SLOWNESS);
                            ((LivingEntity) entity).damage(BASE_DAMAGE + bonusDamage() - reduceDamage(entity), player);
                            count.getAndIncrement();
                        });
                int i = count.get();
                if (i != 0) {
                    adjustFallDistance();
                }
                spawnWebs(i);
                summaryHit(player, i);
                this.cancel();
            } else {
                updateTravelLength();
            }
        }

        private double bonusDamage() {
            return Math.min(travelLength / BONUS_DISTANCE * BONUS_DAMAGE, MAX_BONUS_DAMAGE);
        }

        private double reduceDamage(Entity entity) {
            return lastLocation.distance(entity.getLocation()) / REDUCE_DISTANCE * REDUCE_DAMAGE;
        }

        private void adjustFallDistance() {
            float fallDistance = player.getFallDistance() * SCALE;
            float v = (float) (fallDistance - 3 - player.getHealth());
            if (v >= 0) {
                fallDistance -= v + 0.5F;
            }
            player.setFallDistance(fallDistance);
        }

        private void updateTravelLength() {
            Location location = player.getEyeLocation();
            travelLength += lastLocation.distance(location);
            lastLocation = location;
        }

        private void spawnWebs(int count) {
            Location location = player.getLocation();
            count = Math.max(8, Math.min(count, 10));
            for (int j = 0; j < count; j++) {
                double angle = Math.toRadians(j * 360.0D / count + location.getYaw());
                Vector vector = new Vector(Math.cos(angle), 1, Math.sin(angle)).normalize().multiply(travelLength / 40.0D);
                player.getWorld().spawnEntity(location, EntityType.FALLING_BLOCK, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
                    FallingBlock fallingBlock = (FallingBlock) entity;
                    fallingBlock.setBlockData(Material.COBWEB.createBlockData());
                    fallingBlock.setCancelDrop(true);
                    fallingBlock.setHurtEntities(false);
                    fallingBlock.setVelocity(vector);
                });
            }
        }
    }
}
