package icu.suc.megawalls78.identity.impl.vex.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SoulBound extends Gathering {

    public SoulBound() {
        super("soul_bound", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("soul_bound");
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerDeath(PlayerDeathEvent event) {
            if (PASSIVE(event.getPlayer())) {
                List<ItemStack> drops = event.getDrops();
                List<ItemStack> keep = event.getItemsToKeep();
                drops.removeIf(itemStack -> {
                    Material type = itemStack.getType();
                    if (type.equals(Material.IRON_INGOT)) {
                        keep.add(itemStack);
                        return true;
                    }
                    if (ItemUtil.recipeContains(type, Material.IRON_INGOT)) {
                        keep.add(itemStack);
                        return true;
                    }
                    return false;
                });
            }
        }
    }
}
