package icu.suc.megawalls78.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

public class FakeLightning extends LightningBolt {

    public FakeLightning(Level world) {
        super(EntityType.LIGHTNING_BOLT, world);
    }

    @Override
    public void tick() { //tick()
        --this.life; // life

        if (this.life < 0) {
            this.discard();
        }
    }
}
