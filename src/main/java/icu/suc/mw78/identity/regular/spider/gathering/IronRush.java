package icu.suc.mw78.identity.regular.spider.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

@Trait("iron_rush")
public final class IronRush extends Gathering {

    public IronRush() {
        super(Internal.class);
    }

    public static final class Internal extends Passive {

        @EventHandler(ignoreCancelled = true)
        public void onBlockBreak(BlockDropItemEvent event) {
            Player player = event.getPlayer();
            if (PASSIVE(player) && condition(player)) {
                handle(event);
            }
        }

        private static boolean condition(Player player) {
            return Tag.ITEMS_SHOVELS.isTagged(PlayerUtil.getPlayerMainHand(player).getType());
        }

        private static void handle(BlockDropItemEvent event) {
            BlockUtil.addDrops(event, ItemStack.of(Material.IRON_INGOT));
        }
    }
}
