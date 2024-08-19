package icu.suc.megawalls78.entity;

import icu.suc.megawalls78.entity.pathfinder.HurtByOtherTeamTargetGoal;
import icu.suc.megawalls78.entity.pathfinder.NearestAttackableOtherTeamTargetGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TeamSpider extends Spider {

    public TeamSpider(Level world) {
        super(EntityType.SPIDER, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Armadillo.class, 6.0F, 1.0D, 1.2D, (entityliving) -> !((Armadillo) entityliving).isScared()));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new SpiderAttackGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByOtherTeamTargetGoal(this));
        this.targetSelector.addGoal(2, new SpiderOtherTeamTargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(3, new SpiderOtherTeamTargetGoal<>(this, IronGolem.class));
    }

    private static class SpiderAttackGoal extends MeleeAttackGoal {

        public SpiderAttackGoal(Spider spider) {
            super(spider, 1.0D, true);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.mob.isVehicle();
        }

        @Override
        public boolean canContinueToUse() {
            float f = this.mob.getLightLevelDependentMagicValue();

            if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
                this.mob.setTarget(null);
                return false;
            } else {
                return super.canContinueToUse();
            }
        }
    }

    private static class SpiderOtherTeamTargetGoal<T extends LivingEntity> extends NearestAttackableOtherTeamTargetGoal<T> {

        public SpiderOtherTeamTargetGoal(Spider spider, Class<T> targetEntityClass) {
            super(spider, targetEntityClass, true);
        }

        @Override
        public boolean canUse() {
            float f = this.mob.getLightLevelDependentMagicValue();

            return !(f >= 0.5F) && super.canUse();
        }
    }
}
