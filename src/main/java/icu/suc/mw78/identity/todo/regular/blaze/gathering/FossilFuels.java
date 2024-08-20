package icu.suc.mw78.identity.todo.regular.blaze.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.BlockUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FossilFuels extends Gathering {

    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 50, 2);

    public FossilFuels() {
        super("fossil_fuels", Internal.class);
    }

    public static final class Internal extends CooldownPassive {

        public Internal() {
            super("fossil_fuels", 10000L);
        }

        @EventHandler(ignoreCancelled = true)
        public void onBreakBlock(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (PASSIVE(player) && COOLDOWN() && condition(event)) {
                potion(player);
                COOLDOWN_RESET();
            }
        }

        private static boolean condition(BlockBreakEvent event) {
            return BlockUtil.isOre(event.getBlock().getType());
        }

        private void potion(Player player) {
            player.addPotionEffect(REGENERATION);
            summaryEffectSelf(player, REGENERATION);
        }
    }
}
