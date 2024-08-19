package icu.suc.mw78.identity.regular.golem.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class Momentum extends Gathering {

    public Momentum() {
        super("momentum", Internal.class);
    }

    public static final class Internal extends ChargePassive {

        public Internal() {
            super("momentum", 4);
        }

        @EventHandler(ignoreCancelled = true)
        public void onBreakBlock(BlockDropItemEvent event) {
            Player player = event.getPlayer();
            if (PASSIVE(player) && condition(event) && CHARGE()) {
                handle(event);
                CHARGE_RESET();
            }
        }

        private static boolean condition(BlockDropItemEvent event) {
            return BlockUtil.isWood(event.getBlockState().getType());
        }

        private static void handle(BlockDropItemEvent event) {
            InventoryUtil.addItem(event.getPlayer(), event, ItemStack.of(Material.IRON_INGOT));
        }
    }
}
