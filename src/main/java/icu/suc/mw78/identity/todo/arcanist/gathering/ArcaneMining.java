package icu.suc.mw78.identity.todo.arcanist.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public final class ArcaneMining extends Gathering {

    private static final int ENERGY = 20;

    public ArcaneMining() {
        super("arcane_mining", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("arcane_mining");
        }

        @EventHandler(ignoreCancelled = true)
        public void onBreakBlock(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (PASSIVE(player) && condition(event)) {
                PLAYER().increaseEnergy(ENERGY);
            }
        }

        private static boolean condition(BlockBreakEvent event) {
            return BlockUtil.isOre(event.getBlock().getType());
        }
    }
}
