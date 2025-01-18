package icu.suc.megawalls78.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class Explosion {

    public static final ExplosionDamageCalculator ONLY_BLOCK = new OnlyBlockCalculator();

    public static void create(World world, Entity source, ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, Level.ExplosionInteraction explosionType, boolean particles) {
        net.minecraft.world.entity.Entity entity = source == null ? null : ((CraftEntity) source).getHandle();
        ServerLevel level = ((CraftWorld) world).getHandle();
        level.explode(entity, net.minecraft.world.level.Explosion.getDefaultDamageSource(level, entity), behavior, x, y, z, power, createFire, explosionType);
    }

    private static class OnlyBlockCalculator extends ExplosionDamageCalculator {

        @Override
        public boolean shouldDamageEntity(net.minecraft.world.level.Explosion explosion, net.minecraft.world.entity.Entity entity) {
            return false;
        }

        @Override
        public float getKnockbackMultiplier(net.minecraft.world.entity.Entity entity) {
            return 0;
        }
    }
}
