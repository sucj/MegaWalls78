package icu.suc.megawalls78.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.myzelyam.api.vanish.VanishAPI;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.*;
import icu.suc.megawalls78.entity.TamedWolf;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

public class EntityUtil {

    private static final Vector[][] SAFE_RIDE = new Vector[][]{
            {new Vector(0.2, 0, 0)},
            {new Vector(-0.2, 0, 0)},
            {new Vector(0, 0, 0.2)},
            {new Vector(0, 0, -0.2)},
            {new Vector(0.2, 0, 0), new Vector(0, 0, 0.2)},
            {new Vector(-0.2, 0, 0), new Vector(0, 0, -0.2)},
            {new Vector(-0.2, 0, 0), new Vector(0, 0, 0.2)},
            {new Vector(0.2, 0, 0), new Vector(0, 0, -0.2)},
    };

    public static Entity spawn(Location location, Type type, Object... data) {
        return spawn(location, type, null, data);
    }

    public static Entity spawn(Location location, Type type, Consumer<? super CraftEntity> consumer, Object... data) {
        try {
            ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();
            List<Class<?>> parameterTypes = Lists.newArrayList(Level.class);
            List<Object> initArgs = Lists.newArrayList(world);
            for (Object object : data) {
                parameterTypes.add(Object.class);
                initArgs.add(object);
            }
            net.minecraft.world.entity.Entity entity = type.getClazz().getConstructor(parameterTypes.toArray(Class[]::new)).newInstance(initArgs.toArray());
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

    public static <O> O getMetadata(Metadatable metadatable, String s, Class<O> clazz) {
        List<MetadataValue> values = metadatable.getMetadata(s);
        if (values.isEmpty()) {
            return null;
        }
        Object o = values.getFirst().value();
        if (o == null) {
            return null;
        }
        if (clazz.isInstance(o)) {
            return clazz.cast(o);
        }
        return null;
    }

    public static <O> O getMetadata(Metadatable metadatable, String s, Class<O> clazz, O o) {
        Object value = getMetadata(metadatable, s, o.getClass());
        if (value == null) {
            setMetadata(metadatable, s, o);
            return o;
        } else {
            return clazz.cast(value);
        }
    }

    public static boolean getMetadata(Metadatable metadatable, String s) {
        return Boolean.TRUE.equals(getMetadata(metadatable, s, Boolean.class));
    }

    public static void setMetadata(Metadatable metadatable, String s, Object o) {
        metadatable.setMetadata(s, new FixedMetadataValue(MegaWalls78.getInstance(), o));
    }

    public static void removeMetadata(Metadatable metadatable, String s) {
        metadatable.removeMetadata(s, MegaWalls78.getInstance());
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

    public static boolean isEntityInFront(Entity a, Entity b) {
        Location aLoc = a.getLocation();
        return aLoc.getDirection().setY(0).normalize().dot(b.getLocation().toVector().subtract(aLoc.toVector()).normalize()) > 0;
    }

    public static boolean isOnGround(Entity entity) {
        net.minecraft.world.entity.Entity mcEntity = ((CraftEntity) entity).getHandle();
        ServerLevel world = ((CraftWorld) entity.getWorld()).getHandle();
        Vec3 movement = mcEntity.getDeltaMovement();
        Vec3 vec3 = adjustMovementForCollisions(world, mcEntity, movement);
        return movement.y() != vec3.y() && movement.y() < 0.0D;
    }

    public static Collection<Entity> getNearbyEntitiesCylinder(Location location, double height, double radius) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        BoundingBox boundingBox = new BoundingBox(x + radius, y, z + radius, x - radius, y + height, z - radius);
        return filterVanished(location.getWorld().getNearbyEntities(boundingBox, entity -> {
            double dx = entity.getX() - x;
            double dz = entity.getZ() - z;
            return Math.sqrt(dx * dx + dz * dz) <= radius;
        }));
    }

    public static Collection<Entity> getNearbyEntities(Entity entity, double x, double y, double z) {
        return filterVanished(entity.getNearbyEntities(x, y, z));
    }

    public static Collection<Entity> getNearbyEntities(Entity entity, double radius) {
        Collection<Entity> entities = getNearbyEntities(entity, radius, radius, radius);
        Vector center = entity.getBoundingBox().getCenter();
        entities.removeIf(e -> !inSphere(e, center, radius));
        return filterVanished(entities);
    }

    public static boolean inSphere(Entity entity, Vector center, double radius) {
        BoundingBox box = entity.getBoundingBox();

        double x = center.getX();
        x = Math.min(x, box.getMaxX());
        x = Math.max(x, box.getMinX());

        double y = center.getY();
        y = Math.min(y, box.getMaxY());
        y = Math.max(y, box.getMinY());

        double z = center.getZ();
        z = Math.min(z, box.getMaxZ());
        z = Math.max(z, box.getMinZ());

        return center.distance(new Vector(x, y, z)) <= radius;
    }

    public static Collection <Entity> getNearbyEntities(World world, BoundingBox box) {
        return filterVanished(world.getNearbyEntities(box));
    }

    private static Collection<Entity> filterVanished(Collection<Entity> entities) {
        entities.removeIf(e -> (e instanceof Player && VanishAPI.isInvisible((Player) e)));
        return entities;
    }

    public static Set<Location> getLocations(World world, BoundingBox box) {
        Set<Location> locations = Sets.newHashSet();
        for (int x = (int) box.getMinX(); x <= box.getMaxX(); x++) {
            for (int y = (int) box.getMinY(); y <= box.getMaxY(); y++) {
                for (int z = (int) box.getMinZ(); z <= box.getMaxZ(); z++) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        return locations;
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

    public static boolean hasPotionEffect(LivingEntity entity, PotionEffect effect) {
        return hasPotionEffect(entity, effect.getType(), effect.getAmplifier());
    }

    public static boolean hasPotionEffect(LivingEntity entity, PotionEffectType type, int amplifier) {
        PotionEffect effect = entity.getPotionEffect(type);
        if (effect == null) {
            return false;
        }
        return effect.getAmplifier() == amplifier;
    }

    public static Vector getPullVector(Entity from, Entity to, boolean add) {
        Location fromLoc = from.getLocation();
        Location toLoc = to.getLocation();
        Vector vector = toLoc.toVector().subtract(fromLoc.toVector()).normalize();
        if (add) {
            vector.add(from.getVelocity());
        }
        return vector;
    }

    public static Vector getPullVector(Entity from, Entity to, double x, double y, double z, boolean add) {
        Location fromLoc = from.getLocation();
        Location toLoc = to.getLocation();
        Vector vector = toLoc.toVector().subtract(fromLoc.toVector());
        vector.setX(vector.getX() / x);
        vector.setY(vector.getY() / y);
        vector.setZ(vector.getZ() / z);
        if (add) {
            vector.add(from.getVelocity());
        }
        return vector;
    }

    public static boolean isMeleeAttack(EntityDamageEvent event) {
        return event.getDamageSource().getDamageType().equals(DamageType.PLAYER_ATTACK) && event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
    }

    public static boolean isArrowAttack(EntityDamageEvent event) {
        return event.getDamageSource().getDirectEntity() instanceof Arrow;
    }

    public static boolean isSweepAttack(EntityDamageEvent event) {
        return event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
    }

    public static double getRandomBodyX(Entity entity, double widthScale) {
        return entity.getX() + entity.getWidth() * (2.0 * RandomUtil.RANDOM.nextDouble() - 1.0) * widthScale;
    }

    public static double getRandomBodyY(Entity entity) {
        return entity.getY() + entity.getHeight() * RandomUtil.RANDOM.nextDouble();
    }

    public static double getRandomBodyZ(Entity entity, double widthScale) {
        return entity.getZ() + entity.getWidth() * (2.0 * RandomUtil.RANDOM.nextDouble() - 1.0) * widthScale;
    }

    public static Location getBackwardLocation(Entity entity, double distance) {
        Location entityLocation = entity.getLocation();
//        Location backwardLocation = entityLocation.clone().add(entityLocation.getDirection().multiply(-1).setY(0).multiply(distance));
//        if (backwardLocation.getBlock().isCollidable() || backwardLocation.clone().add(0, 1, 0).getBlock().isCollidable()) {
//            return entityLocation;
//        }
        return entityLocation.add(entityLocation.getDirection().multiply(-1).setY(0).normalize().multiply(distance));
    }

    public static void addPotionEffect(LivingEntity entity, PotionEffect effect, Entity source) {
        ((CraftLivingEntity) entity).getHandle().addEffect(CraftPotionUtil.fromBukkit(effect), ((CraftEntity) source).getHandle(), EntityPotionEffectEvent.Cause.PLUGIN);
    }

    public static boolean traceableTeamed(Entity traceable, Entity entity) {
        if (((CraftEntity) traceable).getHandle() instanceof TraceableEntity nmsTraceable && nmsTraceable.getOwner() instanceof net.minecraft.world.entity.Entity nms) {
            return Objects.equals(nms.getTeam(), ((CraftEntity) entity).getHandle().getTeam());
        }
        return false;
    }

    public static void safeRide(Entity vehicle, Player player) {
        Location location = vehicle.getLocation();
        if (vehicle.collidesAt(location)) {
            for (Vector[] vectors : SAFE_RIDE) {
                Location safe = location.clone();
                for (Vector vector : vectors) {
                    safe.add(vector);
                }
                if (!vehicle.collidesAt(safe)) {
                    vehicle.teleport(safe);
                    break;
                }
            }
        }
        vehicle.addPassenger(player);
    }

    public enum Type {
        CONTROLLABLE_PIG(ControllablePig.class),
        EXPLODING_SHEEP(ExplodingSheep.class),
        EXPLOSIVE_ARROW(ExplosiveArrow.class),
        FAKE_LIGHTNING(FakeLightning.class),
        GRAPPLING_HOOK(GrapplingHook.class),
        HOMING_ARROW(HomingArrow.class),
        SHADOW_BURST_SKULL(ShadowBurstSkull.class),
        TAMED_WOLF(TamedWolf.class),
        TEAM_SKELETON(TeamSkeleton.class),
        TEAM_SPIDER(TeamSpider.class),
        TEAM_WITHER(MegaWither.class),
        TEAM_ZOMBIFIED_PIGLIN(TeamZombifiedPiglin.class);

        private final Class<? extends net.minecraft.world.entity.Entity> clazz;

        Type(Class<? extends net.minecraft.world.entity.Entity> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends net.minecraft.world.entity.Entity> getClazz() {
            return clazz;
        }
    }
}
