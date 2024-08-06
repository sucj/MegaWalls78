package icu.suc.megawalls78.entity.pathfinder;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class HurtByOtherTeamTargetGoal extends HurtByTargetGoal {

    public HurtByOtherTeamTargetGoal(PathfinderMob mob, Class<?>... noRevengeTypes) {
        super(mob, noRevengeTypes);
    }

    @Override
    protected boolean canAttack(@Nullable LivingEntity target, TargetingConditions targetPredicate) {
        return super.canAttack(target, targetPredicate) && !Objects.equals(target.getTeam(), mob.getTeam());
    }
}
