package icu.suc.megawalls78.identity.impl.moleman.skill;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
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

    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 40, 0);
    private static final double FORWARD = 8.0D;
    private static final double DAMAGE = 7.0D;
    private static final double RANGE = 1.0D;
    private static final long TICK = 8L;
    private static final double STEP = 1.0;

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

    private final class Task extends BukkitRunnable {

        private final Player player;

        private final AtomicInteger count;
        private final Set<UUID> victims;
        private Location location;
        private Vector vector;
        private long tick;

        private Task(Player player) {
            this.player = player;

            this.count = new AtomicInteger();
            this.victims = Sets.newHashSet();
        }

        @Override
        public void run() {
            if (player.isDead()) {
                this.cancel();
                return;
            }

            if (tick >= TICK || distanceForward() >= FORWARD) {
                setVector(new Vector(0, 0, 0));
                this.cancel();
                return;
            }

            World world = player.getWorld();

            if (tick % 2L == 0) {
                world.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }

            setVector(vector);

            BoundingBox box = player.getBoundingBox().expand(RANGE);

            for (Location location : EntityUtil.getLocations(world, box)) {
                if (MegaWalls78.getInstance().getGameManager().getRunner().getAllowedBlocks().contains(location)) {
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
                    .forEach(entity -> {
                        UUID uuid = entity.getUniqueId();
                        if (!victims.contains(uuid)) {
                            ((LivingEntity) entity).damage(DAMAGE);
                            victims.add(uuid);
                            count.getAndIncrement();
                        }
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
