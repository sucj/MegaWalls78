package icu.suc.megawalls78.identity.impl.skeleton.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public final class Efficiency extends Gathering {

    private static final Set<Material> TRIPLE = Set.of(Material.OAK_WOOD, Material.SPRUCE_WOOD, Material.BIRCH_WOOD, Material.JUNGLE_WOOD, Material.ACACIA_WOOD, Material.CHERRY_WOOD, Material.DARK_OAK_WOOD, Material.MANGROVE_WOOD, Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.CHERRY_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.COAL_ORE, Material.IRON_ORE, Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE);
    private static final Set<Material> DOUBLE = Set.of(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE);

    public Efficiency() {
        super("efficiency", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("efficiency");
        }

        @EventHandler
        public void onBlockBreak(BlockDropItemEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Player player = event.getPlayer();
            if (PASSIVE(player)) {
                handle(event);
            }
        }

        private static void handle(BlockDropItemEvent event) {
            Material type = event.getBlockState().getType();
            List<Item> items = event.getItems();
            if (TRIPLE.contains(type)) {
                multiply(items, 3);
            } else if (DOUBLE.contains(type)) {
                multiply(items, 2);
            }
        }

        private static void multiply(List<Item> items, int scale) {
            for (Item item : items) {
                ItemStack itemStack = item.getItemStack();
                itemStack.setAmount(itemStack.getAmount() * scale);
            }
        }
    }
}
