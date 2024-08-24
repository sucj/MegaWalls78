package icu.suc.mw78.identity.regular.golem.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.DamageSource;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

@Trait("iron_punch")
public final class IronPunch extends Skill {

    private static final double RADIUS = 4.5D;
    private static final double DAMAGE = 6.0D;

    private static final Vector VECTOR = new Vector(0.0D, -0.235200005D, 0.0D);

    private static final Effect<Location> EFFECT_SKILL = Effect.create(location -> location.getWorld().playSound(location, Sound.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 1.0F, 1.0F));

    public IronPunch() {
        super(100, 2000L);
    }

    @Override
    protected boolean use0(Player player) {

        Location center = player.getLocation();

        double y = center.getY() - 2;
        AtomicInteger count = new AtomicInteger();
        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> !isValidAllies(player, entity))
                .filter(entity -> entity.getY() >= y)
                .forEach(entity -> {
                    ((Player) entity).damage(DAMAGE, DamageSource.of(DamageType.FALLING_ANVIL, player));
                    count.getAndIncrement();
                });

        int i = count.get();
        if (i == 0) {
            return noTarget(player);
        }

        Location location = center.clone().add(0, 3, 0);
        Vector vector = location.getDirection().setY(0).normalize().multiply(RADIUS - 1);
        for (int j = 0; j < 6; j++) {
            vector.rotateAroundY(Math.toRadians(60));
            player.getWorld().spawnEntity(location.clone().add(vector), EntityType.FALLING_BLOCK, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
                FallingBlock fallingBlock = (FallingBlock) entity;
                fallingBlock.setBlockData(Material.IRON_BLOCK.createBlockData());
                fallingBlock.setCancelDrop(true);
                fallingBlock.setHurtEntities(false);
                fallingBlock.setVelocity(VECTOR);
            });
        }

        EFFECT_SKILL.play(center);

        return summaryHit(player, i);
    }
}
