package icu.suc.megawalls78.listener;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.IdentitySelectEvent;
import icu.suc.megawalls78.gui.IdentityGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IdentityListener implements Listener {

    @EventHandler
    public void preIdentitySelected(IdentitySelectEvent.Pre event) {
        Player player = Bukkit.getPlayer(event.getUuid());
        if (player == null) {
            return;
        }
        MegaWalls78.getInstance().getSkinManager().applySkin(player, event.getIdentity());
    }

    @EventHandler
    public void postIdentitySelected(IdentitySelectEvent.Post event) {
        Player player = Bukkit.getPlayer(event.getUuid());
        if (player == null) {
            return;
        }
        player.getInventory().setItem(0, IdentityGui.trigger(player));
    }
}
