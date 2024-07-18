package icu.suc.megawalls78.identity.impl.spider.gathering;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.Passive;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class IronRush extends Gathering {

    public IronRush() {
        super("iron_rush", Internal.class);
    }

    public static class Internal extends Passive {

        private static final Set<Material> MATERIALS = Set.of(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL);

        public Internal() {
            super("iron_rush");
        }
        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (shouldPassive(player) && MATERIALS.contains(player.getEquipment().getItemInMainHand().getType())) {
                player.getInventory().addItem(ItemStack.of(Material.IRON_INGOT));
            }
        }

        @Override
        public void unregister() {

        }
    }
}
