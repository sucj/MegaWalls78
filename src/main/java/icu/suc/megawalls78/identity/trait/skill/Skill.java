package icu.suc.megawalls78.identity.trait.skill;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.function.Predicate;

public abstract class Skill extends Trait implements IActionbar {

    private final float cost;

    private final long COOLDOWN;

    long COOLDOWN_LAST;

    private final Class<? extends Passive> internal;
    private Passive passive;

    public Skill(String id, float cost, long cooldown) {
        this(id, cost, cooldown, null);
    }

    public Skill(String id, float cost, long cooldown, Class<? extends Passive> internal) {
        super(id, Component.translatable("mw78.skill." + id));
        this.cost = cost;
        this.COOLDOWN = cooldown;
        this.internal = internal;
    }

    public boolean use(Player player) {
        if (!available()) {
            return false;
        }
        if (COOLDOWN()) {
            if (use0(player)) {
                COOLDOWN_RESET();
                return true;
            }
        }
        return false;
    }

    protected boolean available() {
        return PLAYER().getEnergy() >= cost;
    }

    @Override
    public Component acb() {
        return Type.COOLDOWN_STATE.accept(COOLDOWN_REMAIN(), available());
    }

    protected abstract boolean use0(Player player);

    public float getCost() {
        return cost;
    }

    public long getCooldown() {
        return COOLDOWN;
    }

    public Class<? extends Passive> getInternal() {
        return internal;
    }

    public Passive getPassive() {
        return passive;
    }

    public void setPassive(Passive passive) {
        this.passive = passive;
    }

    public enum Trigger {
        SWORD(Action.RIGHT, Tag.ITEMS_SWORDS, true),
        BOW(Action.LEFT, Tag.ITEMS_ENCHANTABLE_BOW, false),
        SHOVEL(Action.RIGHT, Tag.ITEMS_SHOVELS, true),
        AXE(Action.RIGHT, Tag.ITEMS_AXES, true),
        CARROT_ON_A_STICK(Action.RIGHT, Material.CARROT_ON_A_STICK, false);

        private final Action action;
        private final Predicate<Material> filter;
        private final boolean sneak;

        Trigger(Action action, Material material, boolean sneak) {
            this(action, material::equals, sneak);
        }

        Trigger(Action action, Tag<Material> tag, boolean sneak) {
            this(action, tag::isTagged, sneak);
        }

        Trigger(Action action, Predicate<Material> filter, boolean sneak) {
            this.action = action;
            this.filter = filter;
            this.sneak = sneak;
        }

        public boolean isSneak() {
            return sneak;
        }

        private boolean isTriggered(org.bukkit.event.block.Action action, Material material) {
            return this.action.equals(Action.getAction(action)) && this.filter.test(material);
        }

        public static Trigger getTrigger(org.bukkit.event.block.Action action, Material material) {
            for (Trigger value : values()) {
                if (value.isTriggered(action, material)) {
                    return value;
                }
            }
            return null;
        }

        enum Action {
            LEFT(org.bukkit.event.block.Action.LEFT_CLICK_AIR, org.bukkit.event.block.Action.LEFT_CLICK_BLOCK),
            RIGHT(org.bukkit.event.block.Action.RIGHT_CLICK_AIR, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK);

            private final Set<org.bukkit.event.block.Action> actions;

            Action(org.bukkit.event.block.Action... actions) {
                this.actions = Sets.newHashSet(actions);
            }

            private static Action getAction(org.bukkit.event.block.Action action) {
                for (Action value : Action.values()) {
                    if (value.actions.contains(action)) {
                        return value;
                    }
                }
                return null;
            }
        }
    }

    protected long CURRENT() {
        return System.currentTimeMillis();
    }

    protected boolean COOLDOWN() {
        return CURRENT() - COOLDOWN_LAST() >= COOLDOWN;
    }

    protected long COOLDOWN(long delta) {
        return COOLDOWN_LAST += delta;
    }

    protected long COOLDOWN_LAST() {
        return COOLDOWN_LAST;
    }

    protected void COOLDOWN_RESET() {
        COOLDOWN_LAST = CURRENT();
    }

    protected void COOLDOWN_END() {
        COOLDOWN_LAST = 0;
    }

    protected long COOLDOWN_REMAIN() {
        return COOLDOWN - CURRENT() + COOLDOWN_LAST();
    }
}
