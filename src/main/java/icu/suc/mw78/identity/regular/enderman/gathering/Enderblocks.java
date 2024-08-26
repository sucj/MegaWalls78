package icu.suc.mw78.identity.regular.enderman.gathering;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.InventoryUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

@Trait(value = "enderblocks", internal = Enderblocks.Internal.class)
public final class Enderblocks extends Gathering {


    @Trait(charge = 3)
    public static final class Internal extends ChargePassive {

        @EventHandler(ignoreCancelled = true)
        public void onBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            Block block = event.getBlock();
            Location location = block.getLocation();
            Material type = block.getType();
            if (PASSIVE(player) && condition_available() && condition_natural(type) && CHARGE()) {
                Set<Location> adjLocations = Sets.newHashSet();

                for (int i = -1; i < 2; i += 2) {
                    adjLocations.add(location.clone().add(i, 0, 0));
                    adjLocations.add(location.clone().add(0, i, 0));
                    adjLocations.add(location.clone().add(0, 0, i));
                }

                for (Location adjLocation : adjLocations) {
                    if (MegaWalls78.getInstance().getGameManager().getRunner().isAllowedLocation(location)) {
                        Block adjBlock = adjLocation.getBlock();
                        if (adjBlock.getType().equals(type)) {
                            List<ItemStack> leftover = InventoryUtil.addItem(player.getInventory(), adjBlock.getDrops(PlayerUtil.getPlayerMainHand(player)));
                            BlockUtil.breakNaturallyNoDrops(adjBlock);
                            for (ItemStack itemStack : leftover) {
                                adjBlock.getWorld().dropItemNaturally(adjBlock.getLocation(), itemStack);
                            }
                        }
                    }
                }

                CHARGE_RESET();
            }
        }

        private static boolean condition_natural(Material type) {
            return BlockUtil.isNatural(type);
        }

        private static boolean condition_available() {
            return !MegaWalls78.getInstance().getGameManager().getRunner().isDm();
        }

        @Override
        public Component acb() {
            return Type.CHARGE_STATE.accept(CHARGE_COUNT(), CHARGE_GET(), condition_available());
        }
    }
}
