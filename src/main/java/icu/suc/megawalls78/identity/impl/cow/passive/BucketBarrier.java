package icu.suc.megawalls78.identity.impl.cow.passive;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

public final class BucketBarrier extends Passive implements IActionbar {

    private static final long COOLDOWN = 30000L;
    private static final long DURATION = 20000L;
    private static final long PER = 5000L;
    private static final double HEALTH = 20.0D;
    private static final double DAMAGE = 2.0D;
    private static final double SCALE = 0.75D;
    private static final double RADIUS = 0.6D;
    private static final double SPEED = 0.1D;

    private long lastMills;
    private long duration;
    private Location center;
    private ItemDisplay[] barriers;

    public BucketBarrier() {
        super("bucket_barrier");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player player) {
            if (shouldPassive(player) && duration > 0) {
                double damage = event.getFinalDamage();
                if (damage >= DAMAGE) {
                    event.setDamage(damage * SCALE);
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.0F, 2.5F - ((float) duration / DURATION));
                    duration -= PER;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTickStart(ServerTickStartEvent event) {
        if (duration <= 0) {
            long currentMillis = System.currentTimeMillis();
            Player player = getPlayer().getBukkitPlayer();
            if (currentMillis - lastMills >= COOLDOWN && player.getHealth() < HEALTH) {
                lastMills = currentMillis;
                duration = DURATION;
                if (barriers == null) {
                    barriers = new ItemDisplay[4];
                    center = player.getLocation().clone().add(0, 1.0D, 0);
                    for (int i = 0; i < 4; i++) {
                        barriers[i] = (ItemDisplay) center.getWorld().spawnEntity(barrierLocation(i), EntityType.ITEM_DISPLAY);
                        barriers[i].setItemStack(ItemStack.of(Material.MILK_BUCKET));
                        Transformation transformation = barriers[i].getTransformation();
                        barriers[i].setTransformation(new Transformation(transformation.getTranslation(), transformation.getLeftRotation(), transformation.getScale().set(0.8F), transformation.getRightRotation()));
                    }
                } else {
                    for (int i = 0; i < 4; i++) {
                        barriers[i].spawnAt(barrierLocation(i));
                    }
                }
            }
            else if (barriers != null) {
                for (ItemDisplay barrier : barriers) {
                    barrier.remove();
                }
                this.barriers = null;
            }
        }
    }

    private Location barrierLocation(int i) {
        double angle = Math.toRadians(i * 90 + 45);
        double x = center.getX() + RADIUS * Math.cos(angle);
        double z = center.getZ() + RADIUS * Math.sin(angle);
        float yaw = (float) Math.toDegrees(angle) + 90.0F;
        return new Location(center.getWorld(), x, center.getY(), z, yaw, 0);
    }

    @EventHandler
    public void onPlayerTickEnd(ServerTickEndEvent event) {
        if (duration > 0) {
            duration -= 50L;
            for (ItemDisplay barrier : barriers) {
                double angle = Math.toRadians(barrier.getLocation().getYaw()) + SPEED;
                barrier.teleport(new Location(center.getWorld(), center.getX() + RADIUS * Math.cos(angle), center.getY(), center.getZ() + RADIUS * Math.sin(angle), (float) Math.toDegrees(angle) + 90.0F, 0));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (shouldPassive(event.getPlayer())) {
            if (duration > 0) {
                center = event.getTo().clone().add(0, 1.0D, 0);
            }
        }
    }

    @Override
    public void unregister() {
        duration = 0;
    }

    @Override
    public Component acb() {
        return Type.DURATION_COOLDOWN.accept(System.currentTimeMillis(), lastMills, COOLDOWN, duration);
    }
}
