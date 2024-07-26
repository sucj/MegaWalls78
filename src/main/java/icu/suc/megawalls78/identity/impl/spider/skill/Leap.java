package icu.suc.megawalls78.identity.impl.spider.skill;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.impl.spider.passive.Skitter;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.*;
import icu.suc.megawalls78.util.Effect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public final class Leap extends Skill {

    private static final double RADIUS = 4.0D;
    private static final double BASE_DAMAGE = 3.0D;
    private static final double BONUS_DAMAGE = 1.0D;
    private static final double BONUS_DISTANCE = 4.0D;
    private static final double MAX_BONUS_DAMAGE = 5.0D;
    private static final double REDUCE_DAMAGE = 1.0D;
    private static final double REDUCE_DISTANCE = 2.0D;
    private static final float SCALE = 0.5F;
    private static final float DIRECT = 1.5F;

    private static final PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOWNESS, 80, 0);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 1);

    private static final Effect<Player> EFFECT_JUMP = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_SPIDER_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F));
    private static final Effect<Player> EFFECT_LAND = Effect.create(player -> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.7F, 0.5F));

    private Task task;

    public Leap() {
        super("leap", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        boolean run = false; // For continuous jump
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        EFFECT_JUMP.play(player);

        Vector vector = player.getLocation().getDirection();
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
                EFFECT_LAND.play(player);
                player.addPotionEffect(REGENERATION);
                AtomicInteger count = new AtomicInteger();
                EntityUtil.getNearbyEntities(player, RADIUS).stream()
                        .filter(entity -> entity instanceof LivingEntity)
                        .filter(entity -> !(entity instanceof Wither))
                        .filter(entity -> !PlayerUtil.isValidAllies(player, entity))
                        .forEach(entity -> {
                            ((LivingEntity) entity).addPotionEffect(SLOWNESS);
                            double damage = BASE_DAMAGE + bonusDamage() - reduceDamage(entity);
                            if (entity.getBoundingBox().overlaps(player.getBoundingBox())) {
                                damage *= DIRECT;
                            }
                            ((LivingEntity) entity).damage(damage, DamageSource.of(DamageType.PLAYER_EXPLOSION, player));
                            count.getAndIncrement();
                        });
                int i = count.get();
                if (i != 0) {
                    adjustFallDistance();
                }
                spawnWebs(i);
                Location location = player.getLocation();
                breakBlock(location.clone().add(0, 1, 0));
                breakBlock(location);
                breakBlock(location.clone().add(0, -1, 0));
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

        private void breakBlock(Location location) {
            for (Block block : BlockUtil.getRoundBlocks(location, RADIUS)) {
                if (MegaWalls78.getInstance().getGameManager().getRunner().getAllowedBlocks().contains(block.getLocation())) {
                    if (BlockUtil.isDestroyable(block)) {
                        BlockUtil.breakNaturally(block);
                    }
                }
            }
        }
    }
}
