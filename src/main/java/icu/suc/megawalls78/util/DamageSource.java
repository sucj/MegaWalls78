package icu.suc.megawalls78.util;

import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;

public class DamageSource {

    public static org.bukkit.damage.DamageSource of(DamageType type, Entity entity) {
        return org.bukkit.damage.DamageSource.builder(type).withDirectEntity(entity).withCausingEntity(entity).build();
    }
}
