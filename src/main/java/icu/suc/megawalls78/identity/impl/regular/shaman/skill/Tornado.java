package icu.suc.megawalls78.identity.impl.regular.shaman.skill;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.DamageSource;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class Tornado extends Skill {

    private static final int DURATION = 100;
    private static final double RADIUS = 3;
    private static final double HEIGHT = 4;
    private static final double DAMAGE = 1.75D;
    private static final int ENERGY = 5;
    private static final int MAX = 3;

    private static final Effect<Location> EFFECT_SKILL = Effect.create(location -> location.getWorld().playSound(location, Sound.ENTITY_BREEZE_IDLE_AIR, SoundCategory.AMBIENT, 0.5F, 0.5F));

    private final List<Task> tasks = Lists.newArrayList();

    public Tornado() {
        super("tornado", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        if (tasks.size() >= MAX) {
            tasks.removeFirst().cancel();
        }

        Task task = new Task(player);
        tasks.add(task);
        task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);

        return true;
    }

    private final class Task extends DurationTask {

        private final Location location;
        private final Vector vector;

        private double angle;

        public Task(Player player) {
            super(player, DURATION);

            location = player.getLocation();
            vector = location.getDirection().setY(0).normalize().multiply(0.05);
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            super.run();

            for (int i = 0; i < 4; i++) {
                for (double j = 1; j < HEIGHT; j += 0.5) {
                    double x = Math.cos(angle + j) * j * 0.75;
                    double z = Math.sin(angle + j) * j * 0.75;
                    ParticleUtil.spawnParticle(player.getWorld(), Particle.FIREWORK, location.clone().add(x, j, z), 1, 0, 0, 0, 0);
                }
            }

            if (tick % 20 == 0) {
                AtomicInteger count = new AtomicInteger();
                EntityUtil.getNearbyEntitiesCylinder(location, HEIGHT, RADIUS).stream()
                        .filter(entity -> entity instanceof Player)
                        .filter(entity -> !isValidAllies(player, entity))
                        .forEach(entity -> {
                            ((Player) entity).damage(DAMAGE, DamageSource.of(DamageType.WIND_CHARGE, player));
                            count.getAndIncrement();
                        });
                if (count.get() > 0) {
                    PLAYER().increaseEnergy(ENERGY);
                }
                EFFECT_SKILL.play(location);
            }

            angle += Math.PI / 16;
            location.add(vector);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            tasks.remove(this);
            super.cancel();
        }
    }
}
