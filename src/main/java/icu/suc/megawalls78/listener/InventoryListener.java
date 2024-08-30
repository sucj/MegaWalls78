package icu.suc.megawalls78.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.gui.*;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.InventoryUtil;
import icu.suc.megawalls78.util.ItemUtil;
import icu.suc.megawalls78.util.Scheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryListener implements Listener {

    public static final Map<UUID, Integer> LAST_SLOTS = Maps.newHashMap();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            Inventory inventory = event.getClickedInventory();
            if (TriggerGui.INVENTORIES.contains(inventory)) {
                TriggerGui.handle(player, event.getSlot());
            }
            else if (gameManager.inFighting()) {
                switch (event.getClick()) {
                    case SHIFT_LEFT:
                    case SHIFT_RIGHT: {
                        if (inventory != null && inventory.getType().equals(InventoryType.PLAYER) && !event.getInventory().getType().equals(InventoryType.CRAFTING) && ItemUtil.mw78SoulBound(event.getCurrentItem())) {
                            event.setCancelled(true);
                        }
                        return;
                    }
                    case NUMBER_KEY: {
                        PlayerInventory playerInventory = player.getInventory();
                        int hotbarButton = event.getHotbarButton();
                        ItemStack playerInventoryItem = playerInventory.getItem(hotbarButton);
                        if (inventory != null && !inventory.getType().equals(InventoryType.PLAYER) && ItemUtil.mw78SoulBound(playerInventoryItem)) {
                            List<Integer> slots = Lists.newArrayList();
                            for (int i = 0; i < playerInventory.getSize(); i++) {
                                ItemStack itemStack = playerInventory.getItem(i);
                                if (itemStack == null || itemStack.isEmpty()) {
                                    slots.add(i);
                                }
                            }
                            if (slots.size() >= 2) {
                                int i = slots.getFirst();
                                playerInventory.setItem(hotbarButton, null);
                                if (i == hotbarButton) {
                                    playerInventory.setItem(hotbarButton + 1, playerInventoryItem);
                                } else {
                                    playerInventory.setItem(i, playerInventoryItem);
                                }
                                Scheduler.runTask(player::updateInventory);
                                break;
                            }
                            event.setCancelled(true);
                        }
                        return;
                    }
                    case SWAP_OFFHAND: {
                        if (inventory != null && !inventory.getType().equals(InventoryType.PLAYER) && ItemUtil.mw78SoulBound(player.getInventory().getItemInOffHand())) {
                            event.setCancelled(true);
                        }
                        return;
                    }
                    case DROP:
                    case CONTROL_DROP: {
                        if (inventory != null && inventory.getType().equals(InventoryType.PLAYER) && ItemUtil.mw78SoulBound(event.getCurrentItem())) {
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
                if ((inventory == null || !inventory.getType().equals(InventoryType.PLAYER)) && ItemUtil.mw78SoulBound(event.getCursor())) {
                    event.setCancelled(true);
                    return;
                }
                if (inventory instanceof PlayerInventory) {
                    if (ItemUtil.mw78SoulBound(event.getCurrentItem()) && !LAST_SLOTS.containsKey(player.getUniqueId())) {
                        LAST_SLOTS.put(player.getUniqueId(), event.getSlot());
                    } else if (!ItemUtil.mw78SoulBound(event.getCurrentItem()) && LAST_SLOTS.containsKey(player.getUniqueId())) {
                        LAST_SLOTS.put(player.getUniqueId(), event.getSlot());
                    }
                }
            }
            else if (gameManager.inWaiting()) {
                event.setCancelled(true);
                if (inventory == null) {
                    return;
                }
                if (IdentityGui.INVENTORIES.containsKey(inventory)) {
                    IdentityGui.handle(player, inventory, event.getSlot());
                } else if (CosmeticsGui.INVENTORIES.contains(inventory)) {
                    CosmeticsGui.handle(player, event.getSlot());
                } else if (SkinGui.INVENTORIES.containsKey(inventory)) {
                    SkinGui.handle(player, inventory, event.getSlot());
                } else if (TeamGui.INVENTORIES.containsKey(inventory)) {
                    TeamGui.handle(player, inventory, event.getSlot());
                } else if (PatternGui.INVENTORIES.containsKey(inventory)) {
                    PatternGui.handle(player, inventory, event.getSlot());
                } else if (TrimGui.INVENTORIES.containsKey(inventory)) {
                    TrimGui.handle(player, inventory, event.getSlot());
                }
            }
            else if (gameManager.isSpectator(player)) {
                event.setCancelled(true);
            }
            else {
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
                    if (slot < inventory.getSize() && ItemUtil.mw78SoulBound(newItems.get(slot))) {
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
            Component title = event.getView().title();
            if (event.getInventory() instanceof CraftInventoryCustom && title.equals(InventoryUtil.ENDERCHEST_TITLE) || title.equals(InventoryUtil.TEAMCHEST_TITLE)) {
                player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            Inventory inventory = event.getInventory();
            Component title = event.getView().title();
            if (inventory instanceof CraftInventoryCustom && title.equals(InventoryUtil.ENDERCHEST_TITLE) || title.equals(InventoryUtil.TEAMCHEST_TITLE)) {
                player.playSound(player, Sound.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            } else {
                CosmeticsGui.INVENTORIES.remove(inventory);
                IdentityGui.INVENTORIES.remove(inventory);
                SkinGui.INVENTORIES.remove(inventory);
                TeamGui.INVENTORIES.remove(inventory);
                PatternGui.INVENTORIES.remove(inventory);
                TrimGui.INVENTORIES.remove(inventory);
                TriggerGui.INVENTORIES.remove(inventory);
            }
            if (player.getItemOnCursor().getType().isAir()) {
                LAST_SLOTS.remove(player.getUniqueId());
            }
        }
    }
}
