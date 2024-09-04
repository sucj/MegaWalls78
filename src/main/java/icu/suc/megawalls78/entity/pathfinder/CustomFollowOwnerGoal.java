package icu.suc.megawalls78.entity.pathfinder;

import icu.suc.megawalls78.entity.custom.tamed.Tamable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class CustomFollowOwnerGoal extends Goal {
    private final Tamable tamable;
    @Nullable
    private LivingEntity owner;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;

    public CustomFollowOwnerGoal(Tamable tameable, double speed, float minDistance, float maxDistance) {
        this.tamable = tameable;
        this.speedModifier = speed;
        this.navigation = tameable.getNavigation();
        this.startDistance = minDistance;
        this.stopDistance = maxDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(tameable.getNavigation() instanceof GroundPathNavigation) && !(tameable.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.tamable.getOwner();
        if (livingEntity == null) {
            return false;
        } else if (this.tamable.unableToMoveToOwner()) {
            return false;
        } else if (this.tamable.distanceToSqr(livingEntity) < (double)(this.startDistance * this.startDistance)) {
            return false;
        } else {
            this.owner = livingEntity;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.navigation.isDone()
            && !this.tamable.unableToMoveToOwner()
            && !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = mob().getPathfindingMalus(PathType.WATER);
        mob().setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        mob().setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        boolean bl = this.tamable.shouldTryTeleportToOwner();
        if (!bl) {
            if (this.tamable.distanceToSqr(this.owner) <= 16 * 16) mob().getLookControl().setLookAt(this.owner, 10.0F, (float) mob().getMaxHeadXRot()); // Paper - Limit pet look distance
        }

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (bl) {
                this.tamable.tryToTeleportToOwner();
            } else {
                this.navigation.moveTo(this.owner, this.speedModifier);
            }
        }
    }

    Mob mob() {
        return ((Mob) tamable);
    }
}
