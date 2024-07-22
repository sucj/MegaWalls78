package icu.suc.megawalls78.identity.impl.spider.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class IronRush extends Gathering {

    public IronRush() {
        super("iron_rush", Internal.class);
    }

    public static class Internal extends Passive {

        public Internal() {
            super("iron_rush");
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (shouldPassive(player) && Tag.ITEMS_SHOVELS.isTagged(PlayerUtil.getPlayerMainHand(player).getType())) {
                player.getInventory().addItem(ItemStack.of(Material.IRON_INGOT));
            }
        }

        @Override
        public void unregister() {

        }
    }
}
