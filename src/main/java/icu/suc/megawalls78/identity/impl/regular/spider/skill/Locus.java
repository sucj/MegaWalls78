package icu.suc.megawalls78.identity.impl.regular.spider.skill;

import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.EntityUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public final class Locus extends Skill {

    public static final String ID = "locus";
    public static final Mode DEFAULT = Mode.ARROW;

    public Locus() {
        super(ID, 0, 0L);
    }

    @Override
    protected boolean use0(Player player) {
        Mode mode = DEFAULT;
        switch (EntityUtil.getMetadata(player, getId(), Mode.class, DEFAULT)) {
            case ARROW -> mode = Mode.ARCED;
            case ARCED -> mode = Mode.ARROW;
        }
        EntityUtil.setMetadata(player, getId(), mode);
        return true;
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
