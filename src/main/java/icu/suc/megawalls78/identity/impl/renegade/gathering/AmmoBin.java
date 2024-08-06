package icu.suc.megawalls78.identity.impl.renegade.gathering;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.InventoryUtil;
import icu.suc.megawalls78.util.RandomUtil;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class AmmoBin extends Gathering {

    private static final int AMOUNT = 30;

    public AmmoBin() {
        super("ammo_bin", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("ammo_bin");
        }

        @EventHandler
        public void onBlockBreak(BlockDropItemEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (PASSIVE(event.getPlayer()) && condition(event)) {
                handleBreak(event);
            }
        }

        @EventHandler
        public void onChestRoll(ChestRollEvent.Post event) {
            if (PASSIVE(event.getPlayer())) {
                handleRoll(event);
            }
        }

        private static boolean condition(BlockDropItemEvent event) {
            return BlockUtil.isOre(event.getBlockState().getType());
        }

        private static void handleBreak(BlockDropItemEvent event) {
            BlockState block = event.getBlockState();
            event.getItems().add(block.getWorld().dropItemNaturally(block.getLocation(), ItemStack.of(Material.ARROW)));
        }

        private static void handleRoll(ChestRollEvent.Post event) {
            InventoryUtil.addItemRandomSlot(event.getInventory(), ItemStack.of(Material.ARROW, AMOUNT));
        }
    }
}
