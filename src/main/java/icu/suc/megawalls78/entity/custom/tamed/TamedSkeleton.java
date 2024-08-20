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
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByOtherTeamTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableOtherTeamTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableOtherTeamTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableOtherTeamTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return owner;
    }
}
