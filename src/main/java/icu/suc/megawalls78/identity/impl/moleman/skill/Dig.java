package icu.suc.megawalls78.identity.impl.moleman.skill;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.identity.trait.skill.task.AbstractTask;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.*;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class Dig extends Skill {

    private static final double FORWARD = 8.0D;
    private static final double DAMAGE = 7.0D;
    private static final double RADIUS = 1.0D;
    private static final int TICK = 8;
    private static final double STEP = 1.0;

    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 40, 0);

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.5F, 1.0F));

    private Task task;

    public Dig() {
        super("dig", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {

        boolean run = false; // Like spider leap
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        player.addPotionEffect(RESISTANCE);
        task.resetTimer();
        task.updateVector();

        if (run) {
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
        }

        return true;
    }

    private final class Task extends DurationTask {

        private final AtomicInteger count;
        private final Set<UUID> victims;
        private Location location;
        private Vector vector;
        private long tick;

        private Task(Player player) {
            super(player, TICK);

            this.count = new AtomicInteger();
            this.victims = Sets.newHashSet();
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            super.run();

            if (distanceForward() >= FORWARD) {
                setVector(new Vector(0, 0, 0));
                cancel();
                return;
            }

            if (tick % 2L == 0) {
                EFFECT_SKILL.play(player);
            }

            setVector(vector);

            BoundingBox box = player.getBoundingBox().expand(RADIUS);
            World world = player.getWorld();

            for (Location location : EntityUtil.getLocations(world, box)) {
                GameManager gameManager = MegaWalls78.getInstance().getGameManager();
                if (gameManager.getRunner().getAllowedBlocks().contains(location)) {
                    Block block = location.getBlock();
                    if (BlockUtil.isDestroyable(block)) {
                        BlockUtil.breakNaturally(block);
                    }
                }
            }

            EntityUtil.getNearbyEntities(world, box).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> !(entity instanceof Wither))
                    .filter(entity -> !PlayerUtil.isValidAllies(player, entity))
                    .filter(entity -> !victims.contains(entity.getUniqueId()))
                    .forEach(entity -> {
                        ((LivingEntity) entity).damage(DAMAGE, DamageSource.of(DamageType.FLY_INTO_WALL, player));
                        victims.add(entity.getUniqueId());
                        count.getAndIncrement();
                    });

            tick++;
        }

        public void resetTimer() {
            this.tick = 0;
        }

        public void updateVector() {
            this.location = player.getEyeLocation();
            this.vector = location.getDirection().multiply(STEP);
        }

        public double distanceForward() {
            return location.distance(player.getEyeLocation()) + 2.0D;
        }

        public void setVector(Vector vector) {
            player.setFallDistance(0);
            player.setVelocity(vector);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            summaryHit(player, count.get());
            super.cancel();
        }
    }
}
