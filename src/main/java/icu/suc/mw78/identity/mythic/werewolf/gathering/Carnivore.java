package icu.suc.mw78.identity.mythic.werewolf.gathering;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

@Trait(value = "carnivore", internal = Carnivore.Internal.class)
public class Carnivore extends Gathering {

    private static final int KILL = 2;
    private static final int FINAL_KILL = 4;

    public static final class Internal extends Passive {

        @EventHandler(ignoreCancelled = true)
        public void onPlayerKill(IncreaseStatsEvent.Kill event) {
            if (PASSIVE(event.getPlayer())) {
                handle(event);
            }
        }

        private static void handle(IncreaseStatsEvent.Kill event) {
            Player player = event.getPlayer().getBukkitPlayer();
            if (event.isFinal()) {
                InventoryUtil.addItem(player, event.getEvent(), ItemStack.of(Material.COOKED_BEEF, FINAL_KILL));
            } else {
                InventoryUtil.addItem(player, event.getEvent(), ItemStack.of(Material.COOKED_BEEF, KILL));
            }
        }
    }
}
