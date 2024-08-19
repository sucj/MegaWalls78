package icu.suc.megawalls78.identity.impl.regular.zombie.gathering;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.StateChangeEvent;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class WellTrained extends Gathering {

    private static final PotionEffect HASTE_2 = new PotionEffect(PotionEffectType.HASTE, 100, 1);
    private static final PotionEffect HASTE_3 = new PotionEffect(PotionEffectType.HASTE, PotionEffect.INFINITE_DURATION, 2);

    public WellTrained() {
        super("well_trained", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("well_trained");
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (PASSIVE(player) && condition_break(event)) {
                potion_break(player);
            }
        }

        @EventHandler
        public void onPlayerRespawn(PlayerRespawnEvent event) {
            Player player = event.getPlayer();
            if (PASSIVE(player) && condition_respawn()) {
                potion_respawn(player);
            }
        }

        @EventHandler
        public void onStateChange(StateChangeEvent event) {
            if (condition_state(event)) {
                Player player = PLAYER().getBukkitPlayer();
                if (EntityUtil.hasPotionEffect(player, HASTE_3)) {
                    potion_remove(player);
                }
            }
        }

        private static boolean condition_break(BlockBreakEvent event) {
            return BlockUtil.isNatural(event.getBlock().getType());
        }

        private static boolean condition_respawn() {
            return MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING);
        }

        private static boolean condition_state(StateChangeEvent event) {
            return event.getState().equals(GameState.BUFFING);
        }

        private void potion_break(Player player) {
            player.addPotionEffect(HASTE_2);
            summaryEffectSelf(player, HASTE_2);
        }

        private void potion_respawn(Player player) {
            player.addPotionEffect(HASTE_3);
            summaryEffectSelf(player, HASTE_3);
        }

        private static void potion_remove(Player player) {
            player.removePotionEffect(PotionEffectType.HASTE);
        }
    }
}
