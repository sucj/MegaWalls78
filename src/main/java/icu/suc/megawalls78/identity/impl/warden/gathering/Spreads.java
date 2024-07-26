package icu.suc.megawalls78.identity.impl.warden.gathering;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.Set;

public final class Spreads extends Gathering {

    private static final Effect<Location> EFFECT_SOUND = Effect.create(location -> location.getWorld().playSound(location, Sound.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F));

    public Spreads() {
        super("spreads", Internal.class);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("spreads");
        }

        @EventHandler
        public void onBlockBreak(BlockDropItemEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Player player = event.getPlayer();
            BlockState blockState = event.getBlockState();
            if (PASSIVE(player) && condition(blockState)) {
                Location location = blockState.getLocation();
                int spreads = spreads(location);
                if (spreads > 0) {
                    for (Item item : event.getItems()) {
                        item.getItemStack().add(spreads);
                    }
                    EFFECT_SOUND.play(location);
                }
            }
        }

        private static boolean condition(BlockState blockState) {
            return BlockUtil.isOre(blockState.getType());
        }

        private static int spreads(Location center) {
            Set<Location> locations = Sets.newHashSet();
            for (int i = -1; i < 2; i += 2) {
                locations.add(center.clone().add(i, 0, 0));
                locations.add(center.clone().add(0, i, 0));
                locations.add(center.clone().add(0, 0, i));
            }
            int i = 0;
            for (Location location : locations) {
                if (BlockUtil.isStone(location.getBlock().getType())) {
                    i++;
                }
            }
            return i;
        }
    }
}
