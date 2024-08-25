package icu.suc.mw78.identity.next.vindicator.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.RandomUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

@Trait("lumberjack")
public final class Lumberjack extends Gathering {

    public Lumberjack() {
        super(Internal.class);
    }

    public static final class Internal extends Passive {

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
