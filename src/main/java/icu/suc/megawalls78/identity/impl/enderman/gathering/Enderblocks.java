package icu.suc.megawalls78.identity.impl.enderman.gathering;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
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

import java.util.Set;

public final class Enderblocks extends Gathering {

    public Enderblocks() {
        super("enderblocks", Internal.class);
    }

    public static final class Internal extends Passive implements IActionbar {

        private static final int MAX = 3;

        private int count = 1;

        public Internal() {
            super("enderblocks");
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            Block block = event.getBlock();
            Location location = block.getLocation();
            Material type = block.getType();
            if (shouldPassive(player) && isAvailable() && BlockUtil.isNatural(type)) {
                if (++count > MAX) {
                    Set<Location> adjLocations = Sets.newHashSet();

                    for (int i = -1; i < 2; i += 2) {
                        adjLocations.add(location.clone().add(i, 0, 0));
                        adjLocations.add(location.clone().add(0, i, 0));
                        adjLocations.add(location.clone().add(0, 0, i));
                    }

                    for (Location adjLocation : adjLocations) {
                        if (MegaWalls78.getInstance().getGameManager().getRunner().getAllowedBlocks().contains(adjLocation)) {
                            Block adjBlock = adjLocation.getBlock();
                            if (adjBlock.getType().equals(type)) {
                                InventoryUtil.addItem(player.getInventory(), adjBlock.getDrops(PlayerUtil.getPlayerMainHand(player)));
                                BlockUtil.breakNaturallyNoDrops(adjBlock);
                            }
                        }
                    }

                    count = 1;
                }
            }
        }

        @Override
        public Component acb() {
            return Type.COMBO_DISABLE.accept(count, MAX, isAvailable());
        }

        @Override
        public void unregister() {
            count = 1;
        }

        private boolean isAvailable() {
            return !MegaWalls78.getInstance().getGameManager().getRunner().isDm();
        }
    }
}
