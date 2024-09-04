package icu.suc.megawalls78.entity.custom.tamed;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.NotNull;

public interface Tamable extends OwnableEntity {

    double distanceToSqr(Entity entity);

    default void tryToTeleportToOwner() {
        LivingEntity owner = this.getOwner();

        if (owner != null) {
            this.teleportToAroundBlockPos(owner.blockPosition());
        }
    }

    default boolean shouldTryTeleportToOwner() {
        LivingEntity owner = this.getOwner();

        return owner != null && this.distanceToSqr(owner) >= 144.0D;
    }

    default boolean unableToMoveToOwner() {
        return this.getOwner() != null && this.getOwner().isSpectator();
    }

    default boolean canFlyToOwner() {
        return false;
    }

    RandomSource getRandom();

    default void teleportToAroundBlockPos(BlockPos pos) {
        for (int i = 0; i < 10; ++i) {
            int j = this.getRandom().nextIntBetweenInclusive(-3, 3);
            int k = this.getRandom().nextIntBetweenInclusive(-3, 3);

            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = this.getRandom().nextIntBetweenInclusive(-1, 1);

                if (this.maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return;
                }
            }
        }
    }

    default boolean maybeTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            // CraftBukkit start
            EntityTeleportEvent event = CraftEventFactory.callEntityTeleportEvent((Entity) this, (double) x + 0.5D, (double) y, (double) z + 0.5D);
            if (event.isCancelled() || event.getTo() == null) { // Paper - prevent NP on null event to location
                return false;
            }
            Location to = event.getTo();
            this.moveTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
            // CraftBukkit end
            this.getNavigation().stop();
            return true;
        }
    }

    void moveTo(double x, double y, double z, float yaw, float pitch);

    PathNavigation getNavigation();

    default boolean canTeleportTo(BlockPos pos) {
        PathType pathtype = WalkNodeEvaluator.getPathTypeStatic((Mob) this, pos);

        if (pathtype != PathType.WALKABLE) {
            return false;
        } else {
            BlockState iblockdata = this.level().getBlockState(pos.below());

            if (!this.canFlyToOwner() && iblockdata.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockposition1 = pos.subtract(this.blockPosition());

                return this.level().noCollision((Entity) this, this.getBoundingBox().move(blockposition1));
            }
        }
    }

    @NotNull
    Level level();

    AABB getBoundingBox();

    BlockPos blockPosition();
}
