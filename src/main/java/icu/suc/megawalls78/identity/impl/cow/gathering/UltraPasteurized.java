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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataType;

public final class UltraPasteurized extends Gathering {

    public UltraPasteurized() {
        super("ultra_pasteurized", Internal.class);
    }

    public static class Internal extends Passive implements IActionbar {

        private static final int MAX = 60;
        private static final ItemBuilder MILK = ItemBuilder.of(Material.MILK_BUCKET)
                .setAmount(2)
                .addPrefix(Identity.COW.getName().append(Component.space()))
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setMaxStackSize(64)
                .addPersistentData(ItemUtil.ID, PersistentDataType.STRING, ItemUtil.COW_MILK);

        private int count = 1;

        public Internal() {
            super("ultra_pasteurized");
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (shouldPassive(player) && isAvailable() && BlockUtil.isStone(event.getBlock().getType())) {
                if (++count > MAX) {
                    player.getInventory().addItem(MILK.build());
                    count = 1;
                }
            }
        }

        @Override
        public void unregister() {
            count = 1;
        }

        @Override
        public Component acb() {
            return Type.COMBO_DISABLE.accept(count, MAX, !isAvailable());
        }

        private boolean isAvailable() {
            return MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING);
        }
    }
}
