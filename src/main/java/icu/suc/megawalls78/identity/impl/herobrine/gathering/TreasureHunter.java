package icu.suc.megawalls78.identity.impl.herobrine.gathering;

import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.Passive;
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

        @EventHandler
        public void onChestRollPre(ChestRollEvent.Pre event) {
            if (event.isCancelled()) {
                return;
            }

            if (shouldPassive(event.getPlayer())) {
                event.setProbability(event.getProbability() * SCALE);
            }
        }

        @Override
        public void unregister() {

        }
    }
}
