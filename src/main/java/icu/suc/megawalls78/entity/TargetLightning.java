package icu.suc.megawalls78.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftEntity;

public class TargetLightning extends LightningBolt {

    private final Entity target;

    public TargetLightning(Level world, Object target) {
        super(EntityType.LIGHTNING_BOLT, world);
        this.target = ((CraftEntity) target).getHandle();
    }

    @Override
    public void tick() { //tick()
        --this.life; // life

        if (this.life < 0) {
            this.discard();
        }

        if (this.life >= 0) {
            if (target != null) {
                target.thunderHit(((ServerLevel) this.level()), this);
            }
        }
    }
}
