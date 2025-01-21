package icu.suc.megawalls78.entity.custom.tamed;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TamedWolf extends Wolf {

    private final UUID owner;

    public TamedWolf(Level world, Object owner) {
        super(EntityType.WOLF, world);
        this.owner = (UUID) owner;
        setTame(true, true);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return this.level().getPlayerByUUID(owner);
    }
}
