package icu.suc.mw78.identity.regular.herobrine.gathering;

import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import org.bukkit.event.EventHandler;

@Trait("treasure_hunter")
public final class TreasureHunter extends Gathering {

    private static final double SCALE = 3.0D;

    public TreasureHunter() {
        super(Internal.class);
    }

    public static final class Internal extends Passive {

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
