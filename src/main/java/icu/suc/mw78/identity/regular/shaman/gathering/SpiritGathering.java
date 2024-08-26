package icu.suc.mw78.identity.regular.shaman.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

@Trait(value = "spirit_gathering", internal = SpiritGathering.Internal.class)
public final class SpiritGathering extends Gathering {

    @Trait(charge = 2)
    public static final class Internal extends ChargePassive {

        @EventHandler(ignoreCancelled = true)
        public void onBreakBlock(BlockDropItemEvent event) {
            Player player = event.getPlayer();
            if (PASSIVE(player) && condition(event) && CHARGE()) {
                handle(event);
                CHARGE_RESET();
            }
        }

        private static boolean condition(BlockDropItemEvent event) {
            return Tag.IRON_ORES.isTagged(event.getBlockState().getType());
        }

        private static void handle(BlockDropItemEvent event) {
            BlockUtil.addDrops(event, ItemStack.of(Material.OAK_LOG));
            BlockUtil.addDrops(event, ItemStack.of(Material.COAL));
        }
    }
}
