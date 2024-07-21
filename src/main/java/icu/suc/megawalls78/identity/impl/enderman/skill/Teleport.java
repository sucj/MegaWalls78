package icu.suc.megawalls78.identity.impl.enderman.skill;

import icu.suc.megawalls78.identity.trait.Skill;
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

public class Teleport extends Skill {

    private static final double RANGE = 25.0D;
    private static final int THICKNESS = 10;
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 100, 2);
    private static final PotionEffect WEAKNESS = new PotionEffect(PotionEffectType.WEAKNESS, 100, 3);

    public Teleport() {
        super("teleport", 100, 8000L);
    }

    @Override
    protected boolean use0(Player player) {
        AtomicReference<Player> aNearestPlayer = new AtomicReference<>();
        AtomicReference<Double> nearestDistance = new AtomicReference<>(Double.MAX_VALUE);

        Location fromF = player.getLocation();
        Location fromH = player.getEyeLocation();
        EntityUtil.getNearbyEntities(player, RANGE).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    double distance = fromF.distance(entity.getLocation());
                    if (distance < nearestDistance.get()) {
                        aNearestPlayer.set(((Player) entity));
                        nearestDistance.set(distance);
                    } else if (distance == nearestDistance.get()) {
                        if (aNearestPlayer.get().getHealth() > ((Player) entity).getHealth()) {
                            aNearestPlayer.set(((Player) entity));
                        }
                    }
                });

        Player nearestPlayer = aNearestPlayer.get();
        if (nearestPlayer == null) {
            return noTarget(player);
        }

        ParticleUtil.spawnParticleRandomBody(player, Particle.PORTAL, 8);
        player.getWorld().playSound(fromH, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        Location toF = nearestPlayer.getLocation();
        Location toH = nearestPlayer.getEyeLocation();
        toF.setYaw(fromF.getYaw());
        toF.setPitch(fromF.getPitch());

        player.setFallDistance(0);
        player.teleport(toF);

        ParticleUtil.spawnParticleRandomBody(player, Particle.PORTAL, 8);
        player.getWorld().playSound(toH, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        player.addPotionEffect(SPEED);

        if (shouldWeakness(fromF, toF) || shouldWeakness(fromH, toH)) {
            player.addPotionEffect(WEAKNESS);
        }

        return true;
    }

    private boolean shouldWeakness(Location from, Location to) {
        if (from.equals(to)) {
            return false;
        }
        int distance = (int) Math.min(from.distance(to), RANGE);
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
