package icu.suc.megawalls78.entity;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.pathfinder.PathfinderGoalNearestAttackableTargetTeam;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.function.Predicate;

public class TeamWither extends WitherBoss {

  private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR;
  private static final TargetingConditions TARGETING_CONDITIONS;

  public TeamWither(Level world) {
    super(EntityType.WITHER, world);
    Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(MegaWalls78.getInstance().getConfigManager().witherHealth);
    setHealth(this.getMaxHealth());
    bossEvent.setVisible(false);
  }

  @Override
  protected void registerGoals() {
//        this.goalSelector.addGoal(2, new PathfinderGoalArrowAttack(this, 1.0, 40, 20.0F));
//        this.goalSelector.addGoal(7, new PathfinderGoalRandomLookaround(this));
    this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
    this.targetSelector.addGoal(2, new PathfinderGoalNearestAttackableTargetTeam(this, 0, false, false, LIVING_ENTITY_SELECTOR));
  }

//    @Override
//    protected void customServerAiStep() {
//        int var0;
//        if (this.getInvulnerableTicks() > 0) {
//            var0 = this.getInvulnerableTicks() - 1;
//            this.bossEvent.setProgress(1.0F - (float)var0 / 220.0F);
//            if (var0 <= 0) {
//                this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, net.minecraft.world.level.World.a.MOB);
//                if (!this.isSilent()) {
//                    this.level().globalLevelEvent(1023, this.blockPosition(), 0);
//                }
//            }
//
//            this.setInvulnerableTicks(var0);
//
//        } else {
//            for(var0 = 1; var0 < 3; ++var0) {
//                List<EntityLiving> var2 = this.level().getNearbyEntities(EntityLiving.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0, 8.0, 20.0));
//                if (!var2.isEmpty()) {
//                    EntityLiving var3 = var2.get(this.random.nextInt(var2.size()));
//                    this.setAlternativeTarget(var0, var3.getId());
//                }
//            }
//
//            if (this.getTarget() != null) {
//                this.setAlternativeTarget(0, this.getTarget().getId());
//            } else {
//                this.setAlternativeTarget(0, 0);
//            }
//
//            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
//        }
//    }


  @Override
  public void setDeltaMovement(Vec3 velocity) {
  }

  @Override
  public void setYRot(float var0) {
    super.setYRot(var0);
    setYBodyRot(var0);
  }

  static {
    LIVING_ENTITY_SELECTOR = (var0) -> !var0.getType().is(EntityTypeTags.WITHER_FRIENDS) && var0.attackable();
    TARGETING_CONDITIONS = TargetingConditions.forCombat().range(10.0).selector(LIVING_ENTITY_SELECTOR);
  }
}
