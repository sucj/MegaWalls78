package icu.suc.mw78.identity.regular.arcanist.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

@Trait("arcane_mining")
public final class ArcaneMining extends Gathering {

    private static final int ENERGY = 20;

    public ArcaneMining() {
        super(Internal.class);
    }

    public static final class Internal extends Passive {

        @EventHandler(ignoreCancelled = true)
        public void onBreakBlock(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (PASSIVE(player) && condition(event)) {
                PLAYER().increaseEnergy(ENERGY);
            }
        }

        private static boolean condition(BlockBreakEvent event) {
            Material type = event.getBlock().getType();
            return BlockUtil.isOre(type) || BlockUtil.isRaw(type);
        }
    }
}
