package icu.suc.megawalls78.listener;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.custom.SafeFirework;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.management.GameManager;
import io.papermc.paper.event.entity.TameableDeathMessageEvent;
import org.bukkit.craftbukkit.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class WorldListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        if (gameManager.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }
        GameState state = gameManager.getState();
        switch (state) {
            case WAITING:
            case COUNTDOWN:
            case OPENING:
            case ENDING:
                event.setCancelled(true);
                return;
            default: {
                if (!gameManager.getRunner().isAllowedLocation(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                } else if (state.equals(GameState.PREPARING)) {
                    if (!gameManager.getRunner().getTeamRegion(gameManager.getPlayer(player).getTeam()).contains(event.getBlock().getLocation().toVector())) {
                        event.setCancelled(true);
                    }
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
        GameState state = gameManager.getState();
        switch (state) {
            case WAITING:
            case COUNTDOWN:
            case OPENING:
            case ENDING:
                event.setCancelled(true);
                return;
            default: {
                if (!gameManager.getRunner().isAllowedLocation(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                } else if (state.equals(GameState.PREPARING)) {
                    if (!gameManager.getRunner().getTeamRegion(gameManager.getPlayer(player).getTeam()).contains(event.getBlock().getLocation().toVector())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        switch (gameManager.getState()) {
            case WAITING:
            case COUNTDOWN:
            case ENDING:
                event.setCancelled(true);
                return;
            default: {
                event.blockList().removeIf(block -> !gameManager.getRunner().isAllowedLocation(block.getLocation()));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPortalCreate(PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTameableDeathMessage(TameableDeathMessageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleSafeFirework(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework firework && ((CraftFirework) firework).getHandle() instanceof SafeFirework) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        event.setTotalCookTime(event.getTotalCookTime() / 4);
    }
}
