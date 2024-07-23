package icu.suc.megawalls78.identity.impl.zombie.gathering;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.StateChangeEvent;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.BlockUtil;
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

        @EventHandler
        public void brokenBlock(BlockBreakEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (shouldPassive(event.getPlayer()) && BlockUtil.isNatural(event.getBlock().getType())) {
                event.getPlayer().addPotionEffect(HASTE_2);
            }
        }

        @EventHandler
        public void onPlayerRespawn(PlayerRespawnEvent event) {
            if (shouldPassive(event.getPlayer()) && MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING)) {
                event.getPlayer().addPotionEffect(HASTE_3);
            }
        }

        @EventHandler
        public void onStateChange(StateChangeEvent event) {
            if (event.getState().equals(GameState.BUFFING)) {
                Player player = getPlayer().getBukkitPlayer();
                PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.HASTE);
                if (potionEffect == null || potionEffect.getAmplifier() == 2 && potionEffect.getDuration() == PotionEffect.INFINITE_DURATION) {
                    player.removePotionEffect(PotionEffectType.HASTE);
                }
            }
        }

        @Override
        public void unregister() {

        }
    }
}
