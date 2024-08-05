package icu.suc.megawalls78.entity;

import icu.suc.megawalls78.util.EntityUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicReference;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public class HomingArrow extends NMSArrow {

    public static final String HOMING = "homing";

    private final float radius;

    public HomingArrow(Level world, Object owner, Object stack, Object shotFrom, Object radius) {
        super(world, owner, stack, shotFrom);
        this.radius = (float) radius;
    }

    @Override
    public void tick() {
        super.tick();

        if (!inGround) {
            CraftEntity bukkitArrow = getBukkitEntity();
            Player player = (Player) getOwner().getBukkitEntity();
            AtomicReference<Player> nearestPlayer = new AtomicReference<>();
            AtomicReference<Double> nearestDistance = new AtomicReference<>(Double.MAX_VALUE);
            Location location = bukkitArrow.getLocation();
            EntityUtil.getNearbyEntities(bukkitArrow, radius).stream()
                    .filter(entity -> entity instanceof Player)
                    .filter(entity -> !isValidAllies(player, entity))
                    .forEach(entity -> {
                        double distance = location.distance(entity.getLocation());
                        double nd = nearestDistance.get();
                        if (distance < nd) {
                            nearestPlayer.set(((Player) entity));
                            nearestDistance.set(distance);
                        } else if (distance == nd) {
                            if (nearestPlayer.get().getHealth() > ((Player) entity).getHealth()) {
                                nearestPlayer.set(((Player) entity));
                            }
                        }
                    });
            Player target = nearestPlayer.get();
            if (target == null) {
                return;
            }
            double oLen = getDeltaMovement().length();
            Vector vector = EntityUtil.getPullVector(bukkitArrow, target, false);
            double nLen = vector.length();
            vector.multiply(oLen / nLen);
            bukkitArrow.setVelocity(vector);
        }
    }
}
