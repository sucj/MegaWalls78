package icu.suc.megawalls78.entity.custom.tamed;

import icu.suc.megawalls78.entity.pathfinder.CustomFollowOwnerGoal;
import icu.suc.megawalls78.entity.pathfinder.HurtByOtherTeamTargetGoal;
import icu.suc.megawalls78.entity.pathfinder.NearestAttackableOtherTeamTargetGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TeamZombifiedPiglin extends ZombifiedPiglin implements Tamable {

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private final UUID owner;

    private HurtByTargetGoal pathfinderGoalHurtByTarget;

    public TeamZombifiedPiglin(Level world, Object owner) {
        super(EntityType.ZOMBIFIED_PIGLIN, world);
        this.owner = (UUID) owner;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new CustomFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, pathfinderGoalHurtByTarget = new HurtByOtherTeamTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableOtherTeamTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void startPersistentAngerTimer() {
        Entity entity = ((ServerLevel) this.level()).getEntity(this.getPersistentAngerTarget());
        org.bukkit.event.entity.PigZombieAngerEvent event = new org.bukkit.event.entity.PigZombieAngerEvent((org.bukkit.entity.PigZombie) this.getBukkitEntity(), (entity == null) ? null : entity.getBukkitEntity(), PERSISTENT_ANGER_TIME.sample(this.random));
        this.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.setPersistentAngerTarget(null);
            pathfinderGoalHurtByTarget.stop();
            return;
        }
        this.setRemainingPersistentAngerTime(event.getNewAnger());
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return owner;
    }
}
