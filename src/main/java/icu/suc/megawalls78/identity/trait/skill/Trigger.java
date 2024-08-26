package icu.suc.megawalls78.identity.trait.skill;

import com.google.common.collect.Sets;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.block.Action;

import java.util.Set;
import java.util.function.Predicate;

public enum Trigger {
    SWORD("sword", Action.RIGHT, Tag.ITEMS_SWORDS),
    BOW("bow", Action.LEFT, Tag.ITEMS_ENCHANTABLE_BOW),
    CROSSBOW("crossbow", Action.LEFT, Tag.ITEMS_ENCHANTABLE_CROSSBOW),
    SHOVEL("shovel", Action.RIGHT, Tag.ITEMS_SHOVELS),
    PICKAXE("pickaxe", Action.RIGHT, Tag.ITEMS_PICKAXES),
    AXE("axe", Action.RIGHT, Tag.ITEMS_AXES),
    HOE("hoe", Action.RIGHT, Tag.ITEMS_HOES),
    CARROT_ON_A_STICK("carrot_on_a_stick", Action.RIGHT, Material.CARROT_ON_A_STICK),
    WARPED_FUNGUS_ON_A_STICK("warped_fungus_on_a_stick", Action.RIGHT, Material.WARPED_FUNGUS_ON_A_STICK);

    private final String id;
    private final Component name;
    private final Action action;
    private final Predicate<Material> filter;

    Trigger(String id, Action action, Material material) {
        this(id, action, material::equals);
    }

    Trigger(String id, Action action, Tag<Material> tag) {
        this(id, action, tag::isTagged);
    }

    Trigger(String id, Action action, Predicate<Material> filter) {
        this.id = id;
        this.name = Component.translatable("mw78.trigger." + id);
        this.action = action;
        this.filter = filter;
    }

    public String getId() {
        return id;
    }

    public Component getName() {
        return name;
    }

    public Action getAction() {
        return action;
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

    public enum Action {
        LEFT(Component.keybind("key.attack"), org.bukkit.event.block.Action.LEFT_CLICK_AIR, org.bukkit.event.block.Action.LEFT_CLICK_BLOCK),
        RIGHT(Component.keybind("key.use"), org.bukkit.event.block.Action.RIGHT_CLICK_AIR, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK);

        private final Component name;
        private final Set<org.bukkit.event.block.Action> actions;

        Action(Component name, org.bukkit.event.block.Action... actions) {
            this.name = name;
            this.actions = Sets.newHashSet(actions);
        }

        public Component getName() {
            return name;
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
