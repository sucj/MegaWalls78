package icu.suc.mw78.identity.todo.next.scatha.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

@Trait("grounded")
public final class Grounded extends Passive {

    private static final int STONE = 1;
    private static final int ORE = 10;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event) {
        if (PASSIVE(event.getPlayer())) {
            handle(event);
        }
    }

    private void handle(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (BlockUtil.isStone(type) || BlockUtil.isCobblestone(type)) {
            PLAYER().increaseEnergy(STONE);
        } else if (BlockUtil.isOre(type)) {
            PLAYER().increaseEnergy(ORE);
        }
    }
}
