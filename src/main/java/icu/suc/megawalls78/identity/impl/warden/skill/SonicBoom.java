package icu.suc.megawalls78.identity.impl.warden.skill;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.DamageSource;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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

public final class SonicBoom extends Skill {

    private static final double RANGE = 7.5D;
    private static final double DAMAGE = 10.0D;
    private static final long CHARGE = 1700L;
    private static final int TICK = (int) (CHARGE / 50);
    private static final PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOWNESS, TICK, 2);
    private static final double RADIUS = 0.5D;
    private static final double SCALE = 0.5D;

    private Task task;

    public SonicBoom() {
        super("sonic_boom", 100, 13000L);
    }

    @Override
    protected boolean use0(Player player) {

        boolean run = false; // Like spider leap
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        task.resetCharge();

        if (run) {
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
        }

        return true;
    }

    private final class Task extends BukkitRunnable {

        private final Player player;

        private int charge;
        private double distance;
        private Location location;
        private Vector vector;

        private final Set<UUID> victims;
        private final AtomicInteger count;
        private final AtomicInteger players;

        private Task(Player player) {
            this.player = player;

            this.victims = Sets.newHashSet();
            this.count = new AtomicInteger();
            this.players = new AtomicInteger();
        }

        @Override
        public void run() {
            if (player.isDead()) {
                this.cancel();
                return;
            }

            if (charge >= TICK) {
                boom();
            }

            if (distance > RANGE) {
                this.cancel();
                return;
            }

            playChargeEffect();
            charge++;
        }

        public void resetCharge() {
            player.addPotionEffect(SLOWNESS);
            playChargeSoundEffect();
            charge = 0;
        }

        private void boom() {
            if (location == null) {
                location = player.getEyeLocation();
                vector = location.getDirection();
            }

            EntityUtil.getNearbyEntities(location.getWorld(), BoundingBox.of(location, RADIUS, RADIUS, RADIUS)).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> !(entity instanceof Wither))
                    .filter(entity -> !PlayerUtil.isValidAllies(player, entity))
                    .filter(entity -> !victims.contains(entity.getUniqueId()))
                    .forEach(entity -> {
                        if (entity instanceof Player) {
                            ((Player) entity).damage(reduceDamage(), DamageSource.of(DamageType.SONIC_BOOM, player));
                        } else {
                            ((LivingEntity) entity).damage(DAMAGE, DamageSource.of(DamageType.SONIC_BOOM, player));
                        }
                        victims.add(entity.getUniqueId());
                        count.getAndIncrement();
                    });

            playBoomEffect();

            Location old = location.clone();
            location.add(vector);
            distance += old.distance(location);
        }

        private double reduceDamage() {
            return DAMAGE - DAMAGE * players.getAndIncrement() * SCALE;
        }

        private void playChargeEffect() {
            ParticleUtil.spawnParticle(player.getWorld(), Particle.SHRIEK, player.getLocation(), 1, 1);
        }

        private void playChargeSoundEffect() {
            player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        private void playBoomEffect() {
            ParticleUtil.spawnParticle(location.getWorld(), Particle.SONIC_BOOM, location, 1);
            player.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            summaryHit(player, count.get());
            super.cancel();
        }
    }
}
