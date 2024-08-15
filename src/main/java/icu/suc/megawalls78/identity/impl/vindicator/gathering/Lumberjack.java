package icu.suc.megawalls78.identity.impl.vindicator.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class Lumberjack extends Gathering {

    public Lumberjack() {
        super("lumberjack", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("lumberjack");
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockBreak(BlockDropItemEvent event) {
            if (PASSIVE(event.getPlayer()) && condition(event)) {
                handle(event);
            }
        }

        private static boolean condition(BlockDropItemEvent event) {
            return BlockUtil.isWood(event.getBlockState().getType());
        }

        private static void handle(BlockDropItemEvent event) {
            BlockUtil.addDrops(event, ItemStack.of(Material.IRON_INGOT));
        }
    }
}
