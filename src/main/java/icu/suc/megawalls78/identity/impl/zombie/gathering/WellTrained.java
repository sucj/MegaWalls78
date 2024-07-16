package icu.suc.megawalls78.identity.impl.zombie.gathering;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.StateChangeEvent;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.Passive;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class WellTrained extends Gathering {

    public WellTrained() {
        super("well_trained", null);
    }

    public static class Internal extends Passive {

        private static final PotionEffect HASTE_2 = new PotionEffect(PotionEffectType.HASTE, 100, 1);
        private static final PotionEffect HASTE_3 = new PotionEffect(PotionEffectType.HASTE, PotionEffect.INFINITE_DURATION, 2);
        private static final Set<Material> MATERIALS = Set.of(Material.STONE, Material.DEEPSLATE,
                Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE,
                Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_DIAMOND_ORE,
                Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.CHERRY_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG);

        public Internal() {
            super("well_trained");
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            if (shouldPassive(event.getPlayer()) && MATERIALS.contains(event.getBlock().getType())) {
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
