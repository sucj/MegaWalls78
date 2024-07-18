package icu.suc.megawalls78.util;

import com.google.common.collect.ImmutableList;
import icu.suc.megawalls78.entity.HerobrineLightning;
import icu.suc.megawalls78.entity.TeamWither;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
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

    public static BlockFace getFacingTowards(Block block, Entity entity) {
        double x = entity.getLocation().getX() - block.getX();
        double z = entity.getLocation().getZ() - block.getZ();
        if (Math.abs(x) > Math.abs(z)) {
            return x > 0 ? BlockFace.EAST : BlockFace.WEST;
        } else {
            return z > 0 ? BlockFace.SOUTH : BlockFace.WEST;
        }
    }

    public static boolean isOnGround(Entity entity) {
        net.minecraft.world.entity.Entity mcEntity = ((CraftEntity) entity).getHandle();
        ServerLevel world = ((CraftWorld) entity.getWorld()).getHandle();
        Vec3 movement = mcEntity.getDeltaMovement();
        Vec3 vec3 = adjustMovementForCollisions(world, mcEntity, movement);
        return movement.y() != vec3.y() && movement.y() < 0.0D;
    }

    private static Vec3 adjustMovementForCollisions(ServerLevel world, net.minecraft.world.entity.Entity entity, Vec3 movement) {
        AABB box = entity.getBoundingBox();
        List<VoxelShape> list = world.getEntityCollisions(entity, box.expandTowards(movement));
        Vec3 vec3d = movement.length() == 0.0 ? movement : net.minecraft.world.entity.Entity.collideBoundingBox(entity, movement, box, world, list);
        boolean bl = movement.x() != vec3d.x();
        boolean bl2 = movement.y() != vec3d.y();
        boolean bl3 = movement.z() != vec3d.z();
        boolean bl4 = bl2 && movement.y < 0.0;
        if (entity.maxUpStep() > 0.0F && (bl4 || entity.onGround()) && (bl || bl3)) {
            AABB box2 = bl4 ? box.move(0.0, vec3d.y(), 0.0) : box;
            AABB box3 = box2.expandTowards(movement.x(), entity.maxUpStep(), movement.z());
            if (!bl4) {
                box3 = box3.expandTowards(0.0, -9.999999747378752E-6, 0.0);
            }

            List<VoxelShape> list2 = findCollisionsForMovement(entity, world, list, box3);
            float f = (float)vec3d.y;
            float[] fs = collectStepHeights(box2, list2, entity.maxUpStep(), f);

            for (float g : fs) {
                Vec3 vec3d2 = adjustMovementForCollisions(new Vec3(movement.x(), g, movement.z()), box2, list2);
                if (vec3d2.horizontalDistanceSqr() > vec3d.horizontalDistanceSqr()) {
                    double d = box.minY - box2.minY;
                    return vec3d2.add(0.0, -d, 0.0);
                }
            }
        }

        return vec3d;
    }

    private static float[] collectStepHeights(AABB collisionBox, List<VoxelShape> collisions, float f, float stepHeight) {
        FloatSet floatSet = new FloatArraySet(4);

        for (VoxelShape voxelShape : collisions) {
            DoubleList doubleList = voxelShape.getCoords(Direction.Axis.Y);

            for (double d : doubleList) {
                float g = (float) (d - collisionBox.minY);
                if (!(g < 0.0F) && g != stepHeight) {
                    if (g > f) {
                        break;
                    }

                    floatSet.add(g);
                }
            }
        }

        float[] fs = floatSet.toFloatArray();
        FloatArrays.unstableSort(fs);
        return fs;
    }

    private static List<VoxelShape> findCollisionsForMovement(@Nullable net.minecraft.world.entity.Entity entity, ServerLevel world, List<VoxelShape> regularCollisions, AABB movingEntityBoundingBox) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(regularCollisions.size() + 1);
        if (!regularCollisions.isEmpty()) {
            builder.addAll(regularCollisions);
        }

        WorldBorder worldBorder = world.getWorldBorder();
        boolean bl = entity != null && worldBorder.isInsideCloseToBorder(entity, movingEntityBoundingBox);
        if (bl) {
            builder.add(worldBorder.getCollisionShape());
        }

        builder.addAll(world.getBlockCollisions(entity, movingEntityBoundingBox));
        return builder.build();
    }

    private static Vec3 adjustMovementForCollisions(Vec3 movement, AABB entityBoundingBox, List<VoxelShape> collisions) {
        if (collisions.isEmpty()) {
            return movement;
        } else {
            double d = movement.x();
            double e = movement.y();
            double f = movement.z();
            if (e != 0.0) {
                e = calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions, e);
                if (e != 0.0) {
                    entityBoundingBox = entityBoundingBox.move(0.0, e, 0.0);
                }
            }

            boolean bl = Math.abs(d) < Math.abs(f);
            if (bl && f != 0.0) {
                f = calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
                if (f != 0.0) {
                    entityBoundingBox = entityBoundingBox.move(0.0, 0.0, f);
                }
            }

            if (d != 0.0) {
                d = calculateMaxOffset(Direction.Axis.X, entityBoundingBox, collisions, d);
                if (!bl && d != 0.0) {
                    entityBoundingBox = entityBoundingBox.move(d, 0.0, 0.0);
                }
            }

            if (!bl && f != 0.0) {
                f = calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
            }

            return new Vec3(d, e, f);
        }
    }

    private static double calculateMaxOffset(Direction.Axis axis, AABB box, Iterable<VoxelShape> shapes, double maxDist) {
        VoxelShape voxelShape;
        for(Iterator<VoxelShape> var5 = shapes.iterator(); var5.hasNext(); maxDist = voxelShape.collide(axis, box, maxDist)) {
            voxelShape = var5.next();
            if (Math.abs(maxDist) < 1.0E-7) {
                return 0.0;
            }
        }

        return maxDist;
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
