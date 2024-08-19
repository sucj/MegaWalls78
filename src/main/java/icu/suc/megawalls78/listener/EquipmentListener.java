package icu.suc.megawalls78.listener;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.management.EquipmentManager;
import icu.suc.megawalls78.management.GameManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.CraftingInventory;

public class EquipmentListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        if (gameManager.inWaiting()) {
            return;
        }
        CraftingInventory inventory = event.getInventory();
        for (HumanEntity viewer : inventory.getViewers()) {
            if (viewer instanceof Player player) {
                if (gameManager.isSpectator(player)) {
                    continue;
                }
                EquipmentManager.decorate(inventory.getResult(), gameManager.getPlayer(player));
            }
        }
    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
//    public void onPLayerDropItem(PlayerDropItemEvent event) {
//        EquipmentManager.clear(event.getItemDrop().getItemStack());
//    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerAttemptPickupItemEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        if (gameManager.inWaiting()) {
            return;
        }
        Player player = event.getPlayer();
        GamePlayer gamePlayer = gameManager.getPlayer(player);
        if (gamePlayer == null) {
            return;
        }
        EquipmentManager.decorate(event.getItem().getItemStack(), gamePlayer);
    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
//    public void onPlayerDeath(PlayerDeathEvent event) {
//        for (ItemStack itemStack : event.getDrops()) {
//            EquipmentManager.clear(itemStack);
//        }
//    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
//    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
//        EquipmentManager.clear(event.getPlayer().getEquipment().getItem(event.getHand()));
//    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        if (gameManager.inWaiting()) {
            return;
        }
        Player player = event.getPlayer();
        GamePlayer gamePlayer = gameManager.getPlayer(player);
        if (gamePlayer == null) {
            return;
        }
        EquipmentManager.decorate(event.getArmorStandItem(), gamePlayer);
//        EquipmentManager.clear(event.getPlayerItem());
    }
}
