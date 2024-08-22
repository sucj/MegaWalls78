package icu.suc.mw78.identity.todo.next.scatha.passive;

import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public final class Grounded extends Passive {

    private static final int ENERGY = 10;

    public Grounded() {
        super("grounded");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event) {
        if (PASSIVE(event.getPlayer()) && condition(event)) {
            PLAYER().increaseEnergy(ENERGY);
        }
    }

    private static boolean condition(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        return BlockUtil.isStone(type) || BlockUtil.isOre(type);
    }
}
