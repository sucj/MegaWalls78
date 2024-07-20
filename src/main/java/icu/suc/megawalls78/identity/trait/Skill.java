package icu.suc.megawalls78.identity.trait;

import com.google.common.collect.Sets;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

public abstract class Skill extends Trait implements IActionbar {

    private final int cost;

    private final long cooldown;
    private long lastMills;

    public Skill(String id, int cost, long cooldown) {
        super(id, Component.translatable("mw78.skill." + id));
        this.cost = cost;
        this.cooldown = cooldown;
    }

    public boolean use(Player player) {
        long currentMillis = System.currentTimeMillis();
        if (currentMillis - lastMills >= cooldown) {
            if (use0(player)) {
                lastMills = currentMillis;
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public Component acbValue() {
        return Type.COOLDOWN.accept(System.currentTimeMillis(), lastMills, cooldown);
    }

    protected abstract boolean use0(Player player);

    public int getCost() {
        return cost;
    }

    public long getCooldown() {
        return cooldown;
    }

    public enum Trigger {
        SWORD(Action.RIGHT, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
        BOW(Action.LEFT, Material.BOW, Material.CROSSBOW),
        ENDER_CHEST(Action.RIGHT, Material.ENDER_CHEST);

        private final Action action;
        private final Set<Material> materials;

        Trigger(Action action, Material... materials) {
            this.action = action;
            this.materials = Set.of(materials);
        }

        private boolean isTriggered(org.bukkit.event.block.Action action, Material material) {
            return this.action.equals(Action.getAction(action)) && this.materials.contains(material);
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
            RIGHT(org.bukkit.event.block.Action.RIGHT_CLICK_AIR, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK),
            ;

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
}
