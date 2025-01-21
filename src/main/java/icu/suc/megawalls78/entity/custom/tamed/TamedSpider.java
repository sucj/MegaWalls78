package icu.suc.megawalls78.entity.custom.tamed;

import icu.suc.megawalls78.entity.pathfinder.CustomFollowOwnerGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TamedSpider extends Spider implements Tamable {

    private final UUID owner;

    public TamedSpider(Level world, Object owner) {
        super(EntityType.SPIDER, world);
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