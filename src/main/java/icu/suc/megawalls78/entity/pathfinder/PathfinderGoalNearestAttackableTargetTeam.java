package icu.suc.megawalls78.entity.pathfinder;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class PathfinderGoalNearestAttackableTargetTeam extends NearestAttackableTargetGoal<LivingEntity> {

    public PathfinderGoalNearestAttackableTargetTeam(Mob mob, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, LivingEntity.class, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
    }

    @Override
    protected void findTarget() {
        this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (var0) -> mob.getTeam() == var0.getTeam()), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }
}
