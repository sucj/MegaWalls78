package icu.suc.megawalls78.util;

import icu.suc.megawalls78.entity.HerobrineLightning;
import icu.suc.megawalls78.entity.TeamWither;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class EntityUtil {

    public static Entity spawn(Location location, Type type) {
        return spawn(location, type, null);
    }

    public static Entity spawn(Location location, Type type, Consumer<? super CraftEntity> consumer) {
        try {
            ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();
            net.minecraft.world.entity.Entity entity = type.getClazz().getConstructor(Level.class).newInstance(world);
            entity.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            CraftEntity bukkitEntity = CraftEntity.getEntity(((CraftServer) Bukkit.getServer()), entity);
            if (consumer != null) {
                consumer.accept(bukkitEntity);
            }
            world.addFreshEntity(entity);
            return bukkitEntity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Player getKiller(LivingEntity entity, DamageSource source) {
        if (source.getCausingEntity() instanceof Player player) {
            return player;
        } else {
            return entity.getKiller();
        }
    }

    public enum Type {
        TEAM_WITHER(TeamWither.class),
        HEROBRINE_LIGHTNING(HerobrineLightning.class);

        private final Class<? extends net.minecraft.world.entity.Entity> clazz;

        Type(Class<? extends net.minecraft.world.entity.Entity> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends net.minecraft.world.entity.Entity> getClazz() {
            return clazz;
        }
    }
}
