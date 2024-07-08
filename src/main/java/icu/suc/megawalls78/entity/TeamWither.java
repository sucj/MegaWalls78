package icu.suc.megawalls78.entity;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.pathfinder.PathfinderGoalNearestAttackableTargetTeam;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class TeamWither extends WitherBoss {

    private BossBar bossBar;
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (entity) -> !entity.getType().is(EntityTypeTags.WITHER_FRIENDS) && entity.attackable();
    private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0D).selector(LIVING_ENTITY_SELECTOR);

    private final int[] nextHeadUpdate0;
    private final int[] idleHeadUpdates0;

    private int count;

    public TeamWither(Level world) {
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
        this.targetSelector.addGoal(2, new PathfinderGoalNearestAttackableTargetTeam(this, 0, false, false, LIVING_ENTITY_SELECTOR));
    }

    @Override
    protected void customServerAiStep() {
        int j;

        for (int i = 1; i < 3; ++i) {
            if (this.tickCount >= this.nextHeadUpdate0[i - 1]) {
                this.nextHeadUpdate0[i - 1] = this.tickCount + 10 + this.random.nextInt(10);
                if (this.level().getDifficulty() == Difficulty.NORMAL || this.level().getDifficulty() == Difficulty.HARD) {
                    int k = i - 1;
                    int l = this.idleHeadUpdates0[i - 1];

                    this.idleHeadUpdates0[k] = this.idleHeadUpdates0[i - 1] + 1;
                    if (l > 15) {
                        float f = 10.0F;
                        float f1 = 5.0F;
                        double d0 = Mth.nextDouble(this.random, this.getX() - 10.0D, this.getX() + 10.0D);
                        double d1 = Mth.nextDouble(this.random, this.getY() - 5.0D, this.getY() + 5.0D);
                        double d2 = Mth.nextDouble(this.random, this.getZ() - 10.0D, this.getZ() + 10.0D);

                        this.performRangedAttack(i + 1, d0, d1, d2, true);
                        this.idleHeadUpdates0[i - 1] = 0;
                    }
                }

                j = this.getAlternativeTarget(i);
                if (j > 0) {
                    LivingEntity entityliving = (LivingEntity) this.level().getEntity(j);

                    if (entityliving != null && this.canAttack(entityliving) && this.distanceToSqr((Entity) entityliving) <= 900.0D && this.hasLineOfSight(entityliving)) {
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

        if (this.tickCount % 20 == 0) {
            if (++count == 5) {
                heal(-4);
                count = 0;
            }
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    private void performRangedAttack(int headIndex, double targetX, double targetY, double targetZ, boolean charged) {
        if (!this.isSilent()) {
            this.level().levelEvent((Player) null, 1024, this.blockPosition(), 0);
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
