package icu.suc.megawalls78.entity.custom.tamed;

import icu.suc.megawalls78.entity.pathfinder.CustomFollowOwnerGoal;
import icu.suc.megawalls78.entity.pathfinder.HurtByOtherTeamTargetGoal;
import icu.suc.megawalls78.entity.pathfinder.NearestAttackableOtherTeamTargetGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TamedSkeleton extends Skeleton implements Tamable {

    private final UUID owner;

    public TamedSkeleton(Level world, Object owner) {
        super(EntityType.SKELETON, world);
        this.owner = ((UUID) owner);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new CustomFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        super.registerGoals();
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return owner;
    }
}
