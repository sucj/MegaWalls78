package icu.suc.mw78.identity.regular.blaze.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait("fossil_fuels")
public final class FossilFuels extends Gathering {

    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 50, 2);

    public FossilFuels() {
        super(Internal.class);
    }

    public static final class Internal extends CooldownPassive {

        public Internal() {
            super(10000L);
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
            Material type = event.getBlock().getType();
            return BlockUtil.isOre(type) || BlockUtil.isRaw(type);
        }

        private void potion(Player player) {
            player.addPotionEffect(REGENERATION);
            summaryEffectSelf(player, REGENERATION);
        }
    }
}
