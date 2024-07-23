package icu.suc.megawalls78.identity.impl.warden.gathering;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.Set;

public final class Spreads extends Gathering {

    public Spreads() {
        super("spreads", null);
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("spreads");
        }

        @EventHandler
        public void brokenBlock(BlockDropItemEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Player player = event.getPlayer();
            Block block = event.getBlock();
            if (shouldPassive(player) && isTrigger(block)) {
                int spreads = spreads(block);
                if (spreads > 0) {
                    for (Item item : event.getItems()) {
                        item.getItemStack().add(spreads);
                    }
                    playSoundEffect(block);
                }
            }
        }

        private void playSoundEffect(Block block) {
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        private boolean isTrigger(Block block) {
            return BlockUtil.isOre(block.getType());
        }

        private int spreads(Block block) {
            Set<Location> locations = Sets.newHashSet();
            Location center = block.getLocation();
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

        @Override
        public void unregister() {

        }
    }
}
