package icu.suc.mw78.identity.next.vex.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Trait(value = "soul_bound", internal = SoulBound.Internal.class)
public class SoulBound extends Gathering {

    public static final class Internal extends Passive {

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
