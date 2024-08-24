package icu.suc.mw78.identity.next.warden.skill;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.identity.trait.skill.task.AbstractTask;
import icu.suc.megawalls78.util.DamageSource;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

@Trait("sonic_boom")
public final class SonicBoom extends Skill {

    private static final double RANGE = 7.5D;
    private static final double DAMAGE = 10.0D;
    private static final long CHARGE = 1700L;
    private static final int TICK = (int) (CHARGE / 50);
    private static final PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOWNESS, TICK, 2);
    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, TICK, 2);
    private static final double RADIUS = 1.5D;
    private static final double SCALE = 0.6D;
    private static final PotionEffect DARKNESS = new PotionEffect(PotionEffectType.DARKNESS, 100, 0);

    private Task task;

    public SonicBoom() {
        super(50, 13000L);
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
            task.fire();
        }

        return true;
    }

    private final class Task extends AbstractTask {

        private int charge;
        private double distance;
        private Location location;
        private Vector vector;

        private final Set<UUID> victims;
        private final AtomicInteger count;
        private final AtomicInteger players;

        private Task(Player player) {
            super(player);

            this.victims = Sets.newHashSet();
            this.count = new AtomicInteger();
            this.players = new AtomicInteger();
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
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
            player.addPotionEffect(RESISTANCE);
            summaryEffectSelf(player, SLOWNESS, RESISTANCE);
            playChargeSoundEffect();
            charge = 0;
        }

        private void boom() {
            if (location == null) {
                location = player.getEyeLocation();
                vector = location.getDirection();
            }

            EntityUtil.getNearbyEntitiesSphere(location, RADIUS).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> !(entity instanceof Wither))
                    .filter(entity -> !isValidAllies(player, entity))
                    .forEach(entity -> {
                        if (victims.contains(entity.getUniqueId())) {
                            return;
                        }
                        LivingEntity living = (LivingEntity) entity;
                        EntityUtil.addPotionEffect(living, DARKNESS, player);
                        if (living instanceof Player victim) {
                            victim.damage(damage(), DamageSource.of(DamageType.SONIC_BOOM, player));
                            summaryEffectOther(player, victim, DARKNESS);
                        } else {
                            living.damage(DAMAGE, DamageSource.of(DamageType.SONIC_BOOM, player));
                        }
                        setVelocity(living);
                        victims.add(living.getUniqueId());
                        count.getAndIncrement();
                    });

            playBoomEffect();

            Location old = location.clone();
            location.add(vector);
            distance += old.distance(location);
        }

        private void setVelocity(Entity entity) {
            Vector velocity = vector.clone().multiply(7.0D - 7.0D * count.get() * SCALE);
            double y = velocity.getY();
            if (Math.abs(y) > 0.5D) {
                velocity.setY(0.5D / y);
            }
            double length = velocity.length();
            if (length > 7.0D) {
                velocity.multiply(length / 7.0D);
            }
            entity.setVelocity(velocity.add(entity.getVelocity()));
        }

        private double damage() {
            int i = players.getAndIncrement();
            if (i == 0) {
                return DAMAGE;
            } else {
                return DAMAGE * i * SCALE;
            }
        }

        private void playChargeEffect() {
            ParticleUtil.spawnParticle(player.getWorld(), Particle.SHRIEK, player.getLocation(), 1, 1);
        }

        private void playChargeSoundEffect() {
            player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        private void playBoomEffect() {
            if (location.distance(player.getEyeLocation()) > 0.5) {
                ParticleUtil.spawnParticle(location.getWorld(), Particle.SONIC_BOOM, location, 1);
            }
            player.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            summaryHit(player, count.get());
            super.cancel();
        }
    }
}
