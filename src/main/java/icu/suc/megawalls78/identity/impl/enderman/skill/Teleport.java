package icu.suc.megawalls78.identity.impl.enderman.skill;

import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicReference;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class Teleport extends Skill {

    private static final double RADIUS = 25.0D;
    private static final int THICKNESS = 10;

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 100, 2);
    private static final PotionEffect WEAKNESS = new PotionEffect(PotionEffectType.WEAKNESS, 100, 0);

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> {
        ParticleUtil.spawnParticleRandomBody(player, Particle.PORTAL, 8);
        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    });

    public Teleport() {
        super("teleport", 100, 8000L);
    }

    @Override
    protected boolean use0(Player player) {
        AtomicReference<Player> nearestPlayer = new AtomicReference<>();
        AtomicReference<Double> nearestDistance = new AtomicReference<>(Double.MAX_VALUE);
        AtomicReference<Double> nearestAngle = new AtomicReference<>(1.0D);

        Location fromF = player.getLocation();
        Location fromH = player.getEyeLocation();
        Vector direction = fromH.getDirection();
        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    Location location = entity.getLocation();
                    double angle = direction.angle(location.toVector().subtract(fromF.toVector()));
                    double distance = fromF.distance(location);
                    double na = nearestAngle.get();
                    if (angle < na) {
                        nearestAngle.set(angle);
                        nearestPlayer.set(((Player) entity));
                        nearestDistance.set(distance);
                    } else if (angle == na) {
                        double nd = nearestDistance.get();
                        if (distance < nd) {
                            nearestPlayer.set(((Player) entity));
                            nearestDistance.set(distance);
                        } else if (distance == nd) {
                            if (nearestPlayer.get().getHealth() > ((Player) entity).getHealth()) {
                                nearestPlayer.set(((Player) entity));
                            }
                        }
                    }
                });

        Player victim = nearestPlayer.get();
        if (victim == null) {
            return noTarget(player);
        }

        EFFECT_SKILL.play(player);

        Location toF = victim.getLocation();
        Location toH = victim.getEyeLocation();
        toF.setYaw(fromF.getYaw());
        toF.setPitch(fromF.getPitch());

        player.setFallDistance(0);
        player.teleport(toF);

        EFFECT_SKILL.play(player);

        player.addPotionEffect(SPEED);

        if (weakness(fromF, toF) || weakness(fromH, toH)) {
            player.addPotionEffect(WEAKNESS);
        }

        return true;
    }

    private static boolean weakness(Location from, Location to) {
        if (from.equals(to)) {
            return false;
        }
        int distance = (int) Math.min(from.distance(to), RADIUS);
        if (distance >= THICKNESS) {
            int count = 0;
            Vector start = from.toVector();
            Vector direction = to.toVector().subtract(start).normalize();
            BlockIterator iterator = new BlockIterator(from.getWorld(), start, direction, 0, distance);
            while (iterator.hasNext()) {
                if (iterator.next().isSolid()) {
                    if (++count >= THICKNESS) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
