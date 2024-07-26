package icu.suc.megawalls78.identity.impl.spider.passive;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class Skitter extends Passive implements IActionbar {

    private static final Mode DEFAULT = Mode.ARROW;
    private static final Map<UUID, Mode> PLAYER_MODES = Maps.newHashMap();
    private static final Set<Material> MATERIALS = Set.of(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL);

    public Skitter() {
        super("skitter");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
// If click air, there are always cancelled.
//        if (event.isCancelled()) {
//            return;
//        }
        Player player = event.getPlayer();
        if (PASSIVE(player)) {
            switch (event.getAction()) {
                case RIGHT_CLICK_BLOCK:
                case RIGHT_CLICK_AIR: {
                    if (!player.isSneaking() && MATERIALS.contains(event.getMaterial())) {
                        UUID uuid = player.getUniqueId();
                        switch (getMode(uuid)) {
                            case ARROW -> setMode(uuid, Mode.ARCED);
                            case ARCED -> setMode(uuid, Mode.ARROW);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Component acb() {
        return Type.MODE.accept(getMode(PLAYER().getUuid()).getName());
    }

    public static Mode getMode(UUID uuid) {
        return PLAYER_MODES.computeIfAbsent(uuid, t -> DEFAULT);
    }

    public static void setMode(UUID uuid, Mode mode) {
        PLAYER_MODES.put(uuid, mode);
    }

    public enum Mode {
        ARROW("arrow", vector -> {
            double y = vector.getY();
            vector.multiply(2.4D);
            vector.setY(NuggetMC_Y(y, 0.8D));
        }),
        ARCED("arced", vector -> {
            double y = vector.getY();
            vector.multiply(1.8D);
            vector.setY(NuggetMC_Y(y, 1.2D));
        });

        private final String id;
        private final Consumer<Vector> consumer;
        private final Component name;

        Mode(String id, Consumer<Vector> consumer) {
            this.id = id;
            this.consumer = consumer;
            this.name = Component.translatable("mw78.acb.skitter." + id);
        }

        public String getId() {
            return id;
        }

        public void accept(Vector vector) {
            consumer.accept(vector);
        }

        public Component getName() {
            return name;
        }

        private static double NuggetMC_Y(double oldY, double newY) {
            return 0.2D * Math.pow(oldY, 2) + newY;
        }
    }
}
