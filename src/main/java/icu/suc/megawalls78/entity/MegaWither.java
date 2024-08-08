package icu.suc.megawalls78.entity;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.pathfinder.HurtByOtherTeamTargetGoal;
import icu.suc.megawalls78.entity.pathfinder.NearestAttackableOtherTeamTargetGoal;
import icu.suc.megawalls78.game.GameState;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class MegaWither extends WitherBoss {

    private BossBar bossBar;
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (entity) -> !entity.getType().is(EntityTypeTags.WITHER_FRIENDS) && entity.attackable();
    private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0D).selector(LIVING_ENTITY_SELECTOR);

    private final int[] nextHeadUpdate0;
    private final int[] idleHeadUpdates0;

    public MegaWither(Level world) {
        super(EntityType.WITHER, world);
        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(MegaWalls78.getInstance().getConfigManager().witherHealth);
        setHealth(this.getMaxHealth());
        bossEvent.setVisible(false);

        try {
            nextHeadUpdate0 = (int[]) FieldUtils.readField(this, "nextHeadUpdate", true);
            idleHeadUpdates0 = (int[]) FieldUtils.readField(this, "idleHeadUpdates", true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.targetSelector.addGoal(1, new HurtByOtherTeamTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableOtherTeamTargetGoal<>(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
    }

    @Override
    protected void customServerAiStep() {
        int j;

        for (int i = 1; i < 3; ++i) {
            if (this.tickCount >= this.nextHeadUpdate0[i - 1]) {
                this.nextHeadUpdate0[i - 1] = this.tickCount + 10 + this.random.nextInt(10);

                j = this.getAlternativeTarget(i);
                if (j > 0) {
                    LivingEntity entityliving = (LivingEntity) this.level().getEntity(j);

                    if (entityliving != null && this.canAttack(entityliving) && this.distanceToSqr(entityliving) <= 900.0D && this.hasLineOfSight(entityliving)) {
                        this.performRangedAttack(i + 1, entityliving);
                        this.nextHeadUpdate0[i - 1] = this.tickCount + 40 + this.random.nextInt(20);
                        this.idleHeadUpdates0[i - 1] = 0;
                    } else {
                        this.setAlternativeTarget(i, 0);
                    }
                } else {
                    List<LivingEntity> list = this.level().getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));

                    if (!list.isEmpty()) {
                        LivingEntity entityliving1 = list.get(this.random.nextInt(list.size()));

                        if (CraftEventFactory.callEntityTargetLivingEvent(this, entityliving1, EntityTargetEvent.TargetReason.CLOSEST_ENTITY).isCancelled()) continue; // CraftBukkit
                        this.setAlternativeTarget(i, entityliving1.getId());
                    }
                }
            }
        }

        if (this.getTarget() != null) {
            this.setAlternativeTarget(0, this.getTarget().getId());
        } else {
            this.setAlternativeTarget(0, 0);
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (MegaWalls78.getInstance().getGameManager().getState().equals(GameState.BUFFING)) {
            for (LivingEntity nearbyEntity : this.level().getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(5.0D, 5.0D, 5.0D))) {
                if (this.tickCount % 100 == 0) {
                    if (this.tickCount % 200 == 0) {
                        nearbyEntity.setDeltaMovement(0.0D, 1.0D, 0.0D);
                    } else {
                        Vec3 vec3 = nearbyEntity.getLookAngle();
                        nearbyEntity.setDeltaMovement(-0.5D * vec3.x, 0.0D, -0.5D * vec3.z);
                    }
                    float health = nearbyEntity.getHealth();
                    if (health <= 0 || health / 2 <= 0) {
                        nearbyEntity.hurt(this.damageSources().wither(), random.nextFloat());
                    } else {
                        nearbyEntity.hurt(this.damageSources().wither(), health / 2);
                    }
                }
                if (this.tickCount % 20 == 0) {
                    PlayerTeam team = nearbyEntity.getTeam();
                    if (team != null && team.equals(getTeam())) {
                        nearbyEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1));
                        nearbyEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1));
                    }
                }
            }
        }
    }

    private void performRangedAttack(int headIndex, double targetX, double targetY, double targetZ, boolean charged) {
        if (!this.isSilent()) {
            this.level().levelEvent(null, 1024, this.blockPosition(), 0);
        }

        double d3 = this.getHeadX(headIndex);
        double d4 = this.getHeadY(headIndex);
        double d5 = this.getHeadZ(headIndex);
        double d6 = targetX - d3;
        double d7 = targetY - d4;
        double d8 = targetZ - d5;
        Vec3 vec3d = new Vec3(d6, d7, d8);
        WitherSkull entitywitherskull = new WitherSkull(this.level(), this, vec3d.normalize());

        entitywitherskull.setOwner(this);
        if (charged) {
            entitywitherskull.setDangerous(true);
        }

        entitywitherskull.setPosRaw(d3, d4, d5);
        this.level().addFreshEntity(entitywitherskull);
    }

    private void performRangedAttack(int headIndex, LivingEntity target) {
        this.performRangedAttack(headIndex, target.getX(), target.getY() + (double) target.getEyeHeight() * 0.5D, target.getZ(), headIndex == 0 && this.random.nextFloat() < 0.001F);
    }

    private double getHeadX(int headIndex) {
        if (headIndex <= 0) {
            return this.getX();
        } else {
            float f = (this.yBodyRot + (float) (180 * (headIndex - 1))) * 0.017453292F;
            float f1 = Mth.cos(f);

            return this.getX() + (double) f1 * 1.3D * (double) this.getScale();
        }
    }

    private double getHeadY(int headIndex) {
        float f = headIndex <= 0 ? 3.0F : 2.2F;

        return this.getY() + (double) (f * this.getScale());
    }

    private double getHeadZ(int headIndex) {
        if (headIndex <= 0) {
            return this.getZ();
        } else {
            float f = (this.yBodyRot + (float) (180 * (headIndex - 1))) * 0.017453292F;
            float f1 = Mth.sin(f);

            return this.getZ() + (double) f1 * 1.3D * (double) this.getScale();
        }
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 velocity) {
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (bossBar != null) {
            bossBar.progress(getHealth() / getMaxHealth());
        }
    }

    public void setBossBar(BossBar bossBar) {
        this.bossBar = bossBar;
    }
}
