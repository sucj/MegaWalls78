package icu.suc.megawalls78.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;

public class SafeFirework extends FireworkRocketEntity {
    
    public SafeFirework(Level world) {
        super(EntityType.FIREWORK_ROCKET, world);
    }
}
