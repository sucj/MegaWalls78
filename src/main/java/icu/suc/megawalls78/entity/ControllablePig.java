package icu.suc.megawalls78.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ControllablePig extends Pig {

    public ControllablePig(Level world) {
        super(EntityType.PIG, world);
        steering.setSaddle(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, (itemstack) -> true, false));
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled()) {
            Entity entity = this.getFirstPassenger();

            if (entity instanceof Player player) {
                return player;
            }
        }

        return super.getControllingPassenger();
    }

    @Override
    public boolean isControlledByLocalInstance() {
//        LivingEntity controller = this.getControllingPassenger();
//        if (controller == null || controller.isHolding(Items.CARROT_ON_A_STICK)) {
//            return super.isControlledByLocalInstance();
//        }
        return true; // For no stop when switched item
    }
}
