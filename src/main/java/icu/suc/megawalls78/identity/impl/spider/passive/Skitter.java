package icu.suc.megawalls78.identity.impl.spider.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public final class Skitter extends Passive implements IActionbar {

    public static final Mode DEFAULT = Mode.ARROW;

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
                    if (!player.isSneaking() && condition(event)) {
                        Mode mode = DEFAULT;
                        switch (EntityUtil.getMetadata(player, getId(), Mode.class, DEFAULT)) {
                            case ARROW -> mode = Mode.ARCED;
                            case ARCED -> mode = Mode.ARROW;
                        }
                        EntityUtil.setMetadata(player, getId(), mode);
                    }
                }
            }
        }
    }

    private static boolean condition(PlayerInteractEvent event) {
        return Tag.ITEMS_SHOVELS.isTagged(event.getMaterial());
    }

    @Override
    public Component acb() {
        return Type.MODE.accept(EntityUtil.getMetadata(PLAYER().getBukkitPlayer(), getId(), Mode.class, DEFAULT).getName());
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

        public Component getName() {
            return name;
        }

        public void accept(Vector vector) {
            consumer.accept(vector);
        }

        private static double NuggetMC_Y(double oldY, double newY) {
            return 0.2D * Math.pow(oldY, 2) + newY;
        }
    }
}
