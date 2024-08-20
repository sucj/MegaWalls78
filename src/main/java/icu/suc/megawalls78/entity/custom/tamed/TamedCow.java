package icu.suc.megawalls78.entity.custom.tamed;

import icu.suc.megawalls78.entity.pathfinder.CustomFollowOwnerGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TamedCow extends Cow implements Tamable {

    private final UUID owner;

    public TamedCow(Level world, Object owner) {
        super(EntityType.COW, world);
        this.owner = (UUID) owner;
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
