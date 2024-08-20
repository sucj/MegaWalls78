package icu.suc.megawalls78.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class GrapplingHook extends FishingHook {

    private final double max;

    private final Hook hook;

    public GrapplingHook(Level world, Object thrower, Object max) {
        super(((CraftPlayer) thrower).getHandle(), world, 0, -1);
        this.max = (double) max;

        hook = new Hook(world, getX(), getY(), getZ(), getDeltaMovement().scale(1.5D), this);
        world.addFreshEntity(hook);
        setHookedEntity(hook);
    }

    @Override
    public void tick() {

        Entity owner = getOwner();

        if (owner == null) {
            discard();
            return;
        }

        if (position().distanceTo(owner.position()) > max) {
            discard();
            return;
        }

        super.tick();
    }

    public boolean inGround() {
        return hook.inGround;
    }

    public static class Hook extends ArmorStand {

        private final GrapplingHook grapplingHook;

        private boolean inGround;
        private boolean falling;

        public Hook(Level world, double x, double y, double z, Vec3 velocity, GrapplingHook grapplingHook) {
            super(world, x, y, z);
            this.grapplingHook = grapplingHook;
            setDeltaMovement(velocity);
            setInvulnerable(true);
            setSilent(true);
            setInvisible(true);
            setMarker(true);
        }

        @Override
        public void tick() {
            if (grapplingHook.isRemoved()) {
                discard();
                return;
            }

            baseTick();

            BlockPos blockPos = this.blockPosition();
            BlockState blockState = this.level().getBlockState(blockPos);
            Vec3 vec3;

            if (!blockState.isAir()) {
                VoxelShape shape = blockState.getCollisionShape(this.level(), blockPos);

                if (!shape.isEmpty()) {
                    vec3 = this.position();

                    for (AABB aabb : shape.toAabbs()) {
                        if (aabb.move(blockPos).contains(vec3)) {
                            setDeltaMovement(0, 0, 0);
                            inGround = true;
                            falling = false;
                            break;
                        }
                    }
                }
            }

            if (inGround) {
                if (this.level().noCollision((new AABB(this.position(), this.position())).inflate(0.06D))) {
                    inGround = false;
                    falling = true;
                }
            } else {

                if (falling) {
                    setDeltaMovement(0, getDeltaMovement().y() - 0.05, 0);
                }

                Vec3 movement = this.getDeltaMovement();
                double x = this.getX() + movement.x;
                double y = this.getY() + movement.y;
                double z = this.getZ() + movement.z;

                ProjectileUtil.rotateTowardsMovement(this, 0.2F);

                this.setPos(x, y, z);
                this.checkInsideBlocks();
            }

//            if (!inGround) {
//                HitResult result = ProjectileUtil.getHitResultOnMoveVector(this, entity -> true, ClipContext.Block.COLLIDER);
//                if (result.getType().equals(HitResult.Type.BLOCK)) {
//                    applyGravity();
//                    return;
//                }
//            }
        }

        @Override
        public boolean canUsePortal(boolean allowVehicles) {
            return false;
        }
    }
}
