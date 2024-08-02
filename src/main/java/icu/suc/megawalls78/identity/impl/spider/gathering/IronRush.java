package icu.suc.megawalls78.identity.impl.spider.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class IronRush extends Gathering {

    public IronRush() {
        super("iron_rush", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("iron_rush");
        }

        @EventHandler
        public void onBlockBreak(BlockDropItemEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Player player = event.getPlayer();
            if (PASSIVE(player) && condition(player)) {
                handle(event);
            }
        }

        private static boolean condition(Player player) {
            return Tag.ITEMS_SHOVELS.isTagged(PlayerUtil.getPlayerMainHand(player).getType());
        }

        private static void handle(BlockDropItemEvent event) {
            BlockState block = event.getBlockState();
            event.getItems().add(block.getWorld().dropItemNaturally(block.getLocation(), ItemStack.of(Material.IRON_INGOT)));
        }
    }
}
