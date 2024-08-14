package icu.suc.megawalls78.util;

import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;

import java.util.Set;

public class DamageSource {

    private static Set<DamageType> TRUE_DAMAGES = Set.of(DamageType.GENERIC_KILL, DamageType.SONIC_BOOM);

    public static org.bukkit.damage.DamageSource of(DamageType type, Entity entity) {
        return org.bukkit.damage.DamageSource.builder(type).withDirectEntity(entity).withCausingEntity(entity).build();
    }

    public static boolean isTrueDamage(DamageType type) {
        return TRUE_DAMAGES.contains(type);
    }
}
