package icu.suc.megawalls78.entity.custom.tamed;

import icu.suc.megawalls78.entity.pathfinder.CustomFollowOwnerGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TamedBlaze extends Blaze implements Tamable {

    private final UUID owner;

    public TamedBlaze(Level world, Object owner) {
        super(EntityType.BLAZE, world);
        this.owner = (UUID) owner;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new CustomFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        super.registerGoals();
    }

    @Override
    public boolean unableToMoveToOwner() {
        return this.isPassenger() || this.mayBeLeashed() || Tamable.super.unableToMoveToOwner();
    }

    @Override
    public boolean canFlyToOwner() {
        return true;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return owner;
    }
}
