package icu.suc.megawalls78.listener;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.gui.IdentityGui;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.ItemUtil;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.UUID;

public class InventoryListener implements Listener {

    public static final Map<UUID, Integer> LAST_SLOTS = Maps.newHashMap();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            if (gameManager.inFighting()) {
                Inventory inventory = event.getClickedInventory();
                switch (event.getClick()) {
                    case SHIFT_LEFT:
                    case SHIFT_RIGHT: {
                        if (inventory != null && inventory.getType().equals(InventoryType.PLAYER) && !event.getInventory().getType().equals(InventoryType.CRAFTING) && ItemUtil.isSoulBound(event.getCurrentItem())) {
                            event.setCancelled(true);
                        }
                        return;
                    }
                    case NUMBER_KEY: {
                        if (inventory != null && !inventory.getType().equals(InventoryType.PLAYER) && ItemUtil.isSoulBound(player.getInventory().getItem(event.getHotbarButton()))) {
                            event.setCancelled(true);
                        }
                        return;
                    }
                    case SWAP_OFFHAND: {
                        if (inventory != null && !inventory.getType().equals(InventoryType.PLAYER) && ItemUtil.isSoulBound(player.getInventory().getItemInOffHand())) {
                            event.setCancelled(true);
                        }
                        return;
                    }
                    case DROP:
                    case CONTROL_DROP: {
                        if (inventory != null && inventory.getType().equals(InventoryType.PLAYER) && ItemUtil.isSoulBound(event.getCurrentItem())) {
                            InventoryType.SlotType slotType = event.getSlotType();
                            if (slotType.equals(InventoryType.SlotType.ARMOR) || slotType.equals(InventoryType.SlotType.QUICKBAR)) {
                                int empty = 0;
                                for (ItemStack itemStack : inventory.getStorageContents()) {
                                    if (itemStack == null) {
                                        empty++;
                                    }
                                    if (empty > 0) {
                                        break;
                                    }
                                }
                                if (empty == 0) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                        return;
                    }
                }
                if ((inventory == null || !inventory.getType().equals(InventoryType.PLAYER)) && ItemUtil.isSoulBound(event.getCursor())) {
                    event.setCancelled(true);
                    return;
                }
                if (inventory instanceof PlayerInventory) {
                    if (ItemUtil.isSoulBound(event.getCurrentItem()) && !LAST_SLOTS.containsKey(player.getUniqueId())) {
                        LAST_SLOTS.put(player.getUniqueId(), event.getSlot());
                    } else if (!ItemUtil.isSoulBound(event.getCurrentItem()) && LAST_SLOTS.containsKey(player.getUniqueId())) {
                        LAST_SLOTS.put(player.getUniqueId(), event.getSlot());
                    }
                }
            } else if (gameManager.inWaiting()) {
                event.setCancelled(true);
                Inventory inventory = event.getClickedInventory();
                if (IdentityGui.INVENTORIES.containsKey(inventory)) {
                    IdentityGui.handle(player, inventory, event.getSlot());
                }
            } else if (gameManager.isSpectator(player)) {
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            if (gameManager.inFighting()) {
                Map<Integer, ItemStack> newItems = event.getNewItems();
                Inventory inventory = event.getInventory();
                for (int slot : newItems.keySet()) {
                    if (slot < inventory.getSize() && ItemUtil.isSoulBound(newItems.get(slot))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            Inventory inventory = event.getInventory();
            if (inventory.getType().equals(InventoryType.ENDER_CHEST) && inventory.getLocation() == null) {
                player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            Inventory inventory = event.getInventory();
            if (inventory.getType().equals(InventoryType.ENDER_CHEST) && inventory.getLocation() == null) {
                player.playSound(player, Sound.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            } else IdentityGui.INVENTORIES.remove(inventory);
            if (player.getItemOnCursor().getType().isAir()) {
                LAST_SLOTS.remove(player.getUniqueId());
            }
        }
    }
}
