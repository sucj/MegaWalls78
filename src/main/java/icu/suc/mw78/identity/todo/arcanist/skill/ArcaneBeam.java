package icu.suc.mw78.identity.todo.arcanist.skill;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.*;
import icu.suc.megawalls78.util.Effect;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

public final class ArcaneBeam extends Skill {

    private static final double DAMAGE = 2.0D;
    private static final double DISTANCE = 34.0D;
    private static final double RADIUS = 1.0D;
    private static final int ENERGY = 40;

    private static final Vector[] VECTORS = new Vector[]{
            new Vector(-1, 1, 1), new Vector(0, 1, 1), new Vector(1, 1, 1),
            new Vector(-1, 1, 0), new Vector(0, 1, 0), new Vector(1, 1, 0),
            new Vector(-1, 1, -1), new Vector(0, 1, -1), new Vector(1, 1, -1),
            new Vector(-1, 0, 1), new Vector(0, 0, 1), new Vector(1, 0, 1),
            new Vector(-1, 0, 0), new Vector(0, 0, 0), new Vector(1, 0, 0),
            new Vector(-1, 0, -1), new Vector(0, 0, -1), new Vector(1, 0, -1),
            new Vector(-1, -1, 1), new Vector(0, -1, 1), new Vector(1, -1, 1),
            new Vector(-1, -1, 0), new Vector(0, -1, 0), new Vector(1, -1, 0),
            new Vector(-1, -1, -1), new Vector(0, -1, -1), new Vector(1, -1, -1)
    };

    private static final Effect<Location> EFFECT_SKILL = Effect.create(location -> location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 1.0F, 1.0F));
    private static final Effect<Location> EFFECT_BEAM = Effect.create(location -> ParticleUtil.spawnParticle(location.getWorld(), Particle.FIREWORK, location, 1, 0, 0, 0, 0));
    private static final Effect<Triple<Location, Location, GamePlayer>> EFFECT_EXPLOSION = Effect.create(triple -> {
        Location beam = triple.getLeft();
        GamePlayer gamePlayer = triple.getRight();
        Location location = triple.getMiddle();
        location.setY(beam.getY());
        ParticleUtil.spawnFirework(location, FireworkEffect.builder().flicker(true).withColor(Color.fromRGB(gamePlayer.getTeam().color().value())).build());
    });

    public ArcaneBeam() {
        super("arcane_beam", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        Location eye = player.getEyeLocation();
        Location beam = eye.clone();

        EFFECT_SKILL.play(eye);

        Vector direction = eye.getDirection().multiply(0.5);

        double distance = 0;
        Set<UUID> victims = Sets.newHashSet();
        while (distance < DISTANCE) {
            EntityUtil.getNearbyEntitiesSphere(beam, RADIUS).stream()
                    .filter(entity -> entity instanceof Player)
                    .filter(entity -> !PlayerUtil.isValidAllies(player, entity))
                    .forEach(entity -> {
                        Player victim = (Player) entity;
                        if (victims.contains(victim.getUniqueId())) {
                            return;
                        }
                        victim.damage(DAMAGE, DamageSource.of(DamageType.MAGIC, player));
                        EFFECT_EXPLOSION.play(Triple.of(beam, victim.getLocation(), PLAYER()));
                        victims.add(victim.getUniqueId());
                    });

            if (distance > 0.5D) {
                EFFECT_BEAM.play(beam);
            }

            if (breakBlock(player, beam)) {
                EFFECT_EXPLOSION.play(Triple.of(beam, beam, PLAYER()));
                break;
            }

            beam.add(direction);
            distance = beam.distance(eye);
        }

        return true;
    }

    private boolean breakBlock(Player player, Location location) {

        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        boolean prepare = gameManager.getState().equals(GameState.PREPARING);
        boolean energy = true;
        boolean flag = false;

        for (Vector vector : VECTORS) {
            Block block = location.clone().add(vector).getBlock();
            if (!gameManager.getRunner().isAllowedLocation(block.getLocation())) {
                continue;
            } else if (prepare) {
                if (!gameManager.getRunner().getTeamRegion(gameManager.getPlayer(player).getTeam()).contains(block.getLocation().toVector())) {
                    continue;
                }
            }
            Material type = block.getType();
            if (BlockUtil.isStone(type)) {
                breakBlock(block);
            } else if (BlockUtil.isOre(type)) {
                breakBlock(block);
                if (prepare && energy) {
                    Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> PLAYER().increaseEnergy(ENERGY));
                    energy = false;
                }
                flag = true;
            }
        }

        return flag;
    }

    private static void breakBlock(Block block) {
        if (BlockUtil.isDestroyable(block)) {
            BlockUtil.breakNaturally(block);
        }
    }
}
