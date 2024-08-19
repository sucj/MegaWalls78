package icu.suc.megawalls78.identity.impl.regular.herobrine.gathering;

import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import org.bukkit.event.EventHandler;

public final class TreasureHunter extends Gathering {

    private static final double SCALE = 3.0D;

    public TreasureHunter() {
        super("treasure_hunter", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("treasure_hunter");
        }

        @EventHandler(ignoreCancelled = true)
        public void onChestRoll(ChestRollEvent.Pre event) {
            if (PASSIVE(event.getPlayer())) {
                handle(event);
            }
        }

        private static void handle(ChestRollEvent.Pre event) {
            event.setChance(event.getChance() * SCALE);
        }
    }
}
