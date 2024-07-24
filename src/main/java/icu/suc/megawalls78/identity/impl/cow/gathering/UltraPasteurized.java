package icu.suc.megawalls78.identity.impl.cow.gathering;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class UltraPasteurized extends Gathering {

    private static final int MAX = 60;
    private static final ItemBuilder MILK = ItemBuilder.of(Material.MILK_BUCKET)
            .setAmount(2)
            .addPrefix(Identity.COW.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMaxStackSize(64)
            .addPersistentData(ItemUtil.ID, PersistentDataType.STRING, ItemUtil.COW_MILK);

    public UltraPasteurized() {
        super("ultra_pasteurized", Internal.class);
    }

    public static final class Internal extends Passive implements IActionbar {

        private int count = MAX;

        public Internal() {
            super("ultra_pasteurized");
        }

        @EventHandler
        public void brokenBlock(BlockDropItemEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Player player = event.getPlayer();
            BlockState blockState = event.getBlockState();
            if (shouldPassive(player) && isAvailable() && isTrigger(blockState.getType())) {
                if (++count > MAX) {
                    dropMilk(event.getItems(), player, blockState.getLocation());
                    count = 1;
                }
            }
        }

        private void playSoundEffect(Location location) {
            location.getWorld().playSound(location, Sound.BLOCK_WET_SPONGE_DRIES, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        private void dropMilk(List<Item> items, Player player, Location location) {
            items.add(player.getWorld().dropItemNaturally(location, MILK.build()));
            playSoundEffect(location);
        }

        private boolean isTrigger(Material material) {
            return BlockUtil.isStone(material);
        }

        private boolean isAvailable() {
            return MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING);
        }

        @Override
        public void unregister() {

        }

        @Override
        public Component acb() {
            return Type.COMBO_STATE.accept(count, MAX, isAvailable());
        }
    }
}
