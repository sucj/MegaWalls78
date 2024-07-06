package icu.suc.megawalls78.listener;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class WorldListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    GameManager gameManager = MegaWalls78.getInstance().getGameManager();
    Player player = event.getPlayer();
    if (gameManager.isSpectator(player)) {
      event.setCancelled(true);
      return;
    }
    switch (gameManager.getState()) {
      case WAITING:
      case COUNTDOWN:
      case OPENING:
      case ENDING:
        event.setCancelled(true);
        return;
      default: {
        if (gameManager.getRunner().getProtectedBlocks().contains(event.getBlock().getLocation())) {
          event.setCancelled(true);
        } else if (ItemUtil.isEnderChest(event.getItemInHand())) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(BlockBreakEvent event) {
    GameManager gameManager = MegaWalls78.getInstance().getGameManager();
    Player player = event.getPlayer();
    if (gameManager.isSpectator(player)) {
      event.setCancelled(true);
      return;
    }
    switch (gameManager.getState()) {
      case WAITING:
      case COUNTDOWN:
      case OPENING:
      case ENDING:
        event.setCancelled(true);
        return;
      default: {
        if (gameManager.getRunner().getProtectedBlocks().contains(event.getBlock().getLocation())) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onExplode(EntityExplodeEvent event) {
    GameManager gameManager = MegaWalls78.getInstance().getGameManager();
    switch (gameManager.getState()) {
      case WAITING:
      case COUNTDOWN:
      case ENDING:
        event.setCancelled(true);
        return;
      default: {
        event.blockList().removeIf(block -> gameManager.getRunner().getProtectedBlocks().contains(block.getLocation()));
      }
    }
  }
}
