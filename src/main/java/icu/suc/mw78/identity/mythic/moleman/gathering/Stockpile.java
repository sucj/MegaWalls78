package icu.suc.mw78.identity.mythic.moleman.gathering;

import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public final class Stockpile extends Gathering {

    private static final double SCALE = 3.0D;

    public Stockpile() {
        super("stockpile", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("stockpile");
        }

        @EventHandler
        public void onChestRollPost(ChestRollEvent.Post event) {
            if (PASSIVE(event.getPlayer())) {
                for (ItemStack itemStack : event.getInventory()) {
                    if (itemStack != null && itemStack.getType().equals(Material.IRON_INGOT)) {
                        itemStack.setAmount((int) (itemStack.getAmount() * SCALE));
                    }
                }
            }
        }
    }
}
