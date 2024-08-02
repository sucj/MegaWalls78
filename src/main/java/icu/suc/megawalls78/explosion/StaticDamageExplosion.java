package icu.suc.megawalls78.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StaticDamageExplosion extends Explosion {
    public StaticDamageExplosion(Level world, @Nullable Entity entity, double x, double y, double z, float power, List<BlockPos> affectedBlocks, BlockInteraction destructionType, ParticleOptions particle, ParticleOptions emitterParticle, Holder<SoundEvent> soundEvent) {
        super(world, entity, x, y, z, power, affectedBlocks, destructionType, particle, emitterParticle, soundEvent);
    }

    public StaticDamageExplosion(Level world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, BlockInteraction destructionType, List<BlockPos> affectedBlocks) {
        super(world, entity, x, y, z, power, createFire, destructionType, affectedBlocks);
    }

    public StaticDamageExplosion(Level world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, BlockInteraction destructionType) {
        super(world, entity, x, y, z, power, createFire, destructionType);
    }

    public StaticDamageExplosion(Level world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, BlockInteraction destructionType, ParticleOptions particle, ParticleOptions emitterParticle, Holder<SoundEvent> soundEvent) {
        super(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType, particle, emitterParticle, soundEvent);
    }
}
