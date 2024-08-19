package icu.suc.megawalls78.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.portal.DimensionTransition;
import org.bukkit.craftbukkit.entity.CraftEntity;

import javax.annotation.Nullable;
import java.util.Optional;

public class ExplodingSheep extends Sheep implements TraceableEntity {

    private final Entity owner;
    private boolean usedPortal;
    private boolean sound = true;

    private static final float DEFAULT_RADIUS = 4.0F;
    private static final int DEFAULT_FUSE_TIME = 80;
    private static final ExplosionDamageCalculator USED_PORTAL_DAMAGE_CALCULATOR = new ExplosionDamageCalculator() {
        @Override
        public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power) {
            return !state.is(Blocks.NETHER_PORTAL) && super.shouldBlockExplode(explosion, world, pos, state, power);
        }

        @Override
        public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState blockState, FluidState fluidState) {
            return blockState.is(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlockExplosionResistance(explosion, world, pos, blockState, fluidState);
        }
    };

    public ExplodingSheep(Level world, Object owner) {
        super(EntityType.SHEEP, world);
        this.owner = ((CraftEntity) owner).getHandle();
        setColor(DyeColor.RED);
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable("mw78.entity.exploding_sheep");
    }

    @Override
    public void tick() {
        super.tick();

        if (sound) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.TNT_PRIMED, SoundSource.MASTER, 1.0F, 1.0F);
            sound = false;
        }

        ((ServerLevel) level()).sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0);

        if (tickCount % 20 == 0) {
            setColor(DyeColor.RED);
        } else if (tickCount % 10 == 0) {
            setColor(DyeColor.WHITE);
        }

        if (tickCount >= DEFAULT_FUSE_TIME) {
            discard();
            explode();
        }
    }

    private void explode() {
        this.level().explode(this, Explosion.getDefaultDamageSource(this.level(), this), this.usedPortal ? USED_PORTAL_DAMAGE_CALCULATOR : null, this.getX(), this.getY(0.0625D), this.getZ(), DEFAULT_RADIUS, false, Level.ExplosionInteraction.TNT);
    }

    private void setUsedPortal(boolean teleported) {
        this.usedPortal = teleported;
    }

    @Nullable
    @Override
    public Entity changeDimension(DimensionTransition teleportTarget) {
        Entity entity = super.changeDimension(teleportTarget);

        if (entity instanceof ExplodingSheep sheep) {
            sheep.setUsedPortal(true);
        }

        return entity;
    }

    @Override
    public Entity getOwner() {
        return owner;
    }
}
