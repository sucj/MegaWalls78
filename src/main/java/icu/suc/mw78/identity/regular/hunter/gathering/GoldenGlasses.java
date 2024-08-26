package icu.suc.mw78.identity.regular.hunter.gathering;

import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.InventoryUtil;
import icu.suc.megawalls78.util.RandomUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

@Trait(value = "golden_glasses", internal = GoldenGlasses.Internal.class)
public final class GoldenGlasses extends Gathering {

    private static final double CHANCE = 0.5D;

    public static final class Internal extends Passive {

        @EventHandler
        public void onChestRoll(ChestRollEvent.Post event) {
            if (PASSIVE(event.getPlayer()) && condition()) {
                handle(event);
            }
        }

        private static boolean condition() {
            return RandomUtil.roll(CHANCE);
        }

        private static void handle(ChestRollEvent.Post event) {
            InventoryUtil.addItemRandomSlot(event.getInventory(), ItemStack.of(Material.GOLDEN_APPLE));
        }
    }
}
