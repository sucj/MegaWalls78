package icu.suc.megawalls78.identity.impl.cow.passive;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.identity.trait.passive.DurationCooldownPassive;
import icu.suc.megawalls78.util.Effect;
import io.papermc.paper.util.Tick;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

public final class BucketBarrier extends DurationCooldownPassive {

    private static final long PER = 5000L;
    private static final double HEALTH = 20.0D;
    private static final double DAMAGE = 2.0D;
    private static final double SCALE = 0.75D;
    private static final double SPEED = 0.1D;
    private static final double RADIUS = 0.4D;

    private static final Effect<Pair<Player, Float>> EFFECT_SKILL = Effect.create(pair -> {
        Player player = pair.getLeft();
        Float progress = pair.getRight();
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.0F, 2.5F - progress);
    });

    private Location center;
    private ItemDisplay[] barriers;

    public BucketBarrier() {
        super("bucket_barrier", 30000L, 20000L);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player player && PASSIVE(player) && DURATION() && block(event)) {
            EFFECT_SKILL.play(Pair.of(player, (float) DURATION_REMAIN() / DURATION));
            DURATION(PER);
        }
    }

    @EventHandler
    public void onPLayerTickStart(ServerTickStartEvent event) {
        if (DURATION()) {
            return;
        }

        if (COOLDOWN() && spawnBarriers(PLAYER().getBukkitPlayer())) {
            DURATION_RESET();
        } else {
            removeBarriers();
        }
    }


    @EventHandler
    public void onPLayerTickEnd(ServerTickEndEvent event) {
        if (DURATION()) {
            updateBarriers();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (PASSIVE(player) && DURATION()) {
            center = event.getTo().clone().add(0, player.getEyeHeight() + 0.5, 0);
        }
    }

    private static boolean block(EntityDamageEvent event) {
        double damage = event.getFinalDamage();
        if (damage >= DAMAGE) {
            event.setDamage(damage * SCALE);
            return true;
        }
        return false;
    }

    private boolean spawnBarriers(Player player) {
        if (player != null && player.getHealth() < HEALTH) {
            if (barriers == null) {
                barriers = new ItemDisplay[4];
                center = player.getEyeLocation().clone().add(0, 0.5, 0);
                for (int i = 0; i < 4; i++) {
                    barriers[i] = (ItemDisplay) center.getWorld().spawnEntity(barrierLocation(i), EntityType.ITEM_DISPLAY);
                    barriers[i].setItemStack(ItemStack.of(Material.MILK_BUCKET));
                    Transformation transformation = barriers[i].getTransformation();
                    barriers[i].setTransformation(new Transformation(transformation.getTranslation(), transformation.getLeftRotation(), transformation.getScale().set(0.5F), transformation.getRightRotation()));
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    barriers[i].spawnAt(barrierLocation(i));
                }
            }
            return true;
        }
        return false;
    }

    private void removeBarriers() {
        if (barriers != null) {
            for (ItemDisplay barrier : barriers) {
                barrier.remove();
            }
        }
    }

    private void updateBarriers() {
//        DURATION(Tick.tick().getDuration().toMillis());
        for (ItemDisplay barrier : barriers) {
            double angle = Math.toRadians(barrier.getLocation().getYaw()) + SPEED;
            barrier.teleport(new Location(center.getWorld(), center.getX() + RADIUS * Math.cos(angle), center.getY(), center.getZ() + RADIUS * Math.sin(angle), (float) Math.toDegrees(angle) + 90.0F, 0));
        }
    }

    private void updateCenter(Location location) {
        center = location.clone().add(0, 1.0D, 0);
    }

    private Location barrierLocation(int i) {
        double angle = Math.toRadians(i * 90 + 45);
        double x = center.getX() + RADIUS * Math.cos(angle);
        double z = center.getZ() + RADIUS * Math.sin(angle);
        float yaw = (float) Math.toDegrees(angle) + 90.0F;
        return new Location(center.getWorld(), x, center.getY(), z, yaw, 0);
    }

    @Override
    public void unregister() {
        DURATION_END();
    }
}
