package icu.suc.megawalls78.identity.impl.cow.gathering;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public final class UltraPasteurized extends Gathering {

    public UltraPasteurized() {
        super("ultra_pasteurized", Internal.class);
    }

    public static class Internal extends Passive implements IActionbar {

        private static final int MAX = 60;
        private static final Set<Material> MATERIALS = Set.of(Material.STONE, Material.DEEPSLATE);
        private static final ItemBuilder MILK = ItemBuilder.of(Material.MILK_BUCKET).setAmount(2).addPrefix(Identity.COW.getName().append(Component.space())).addDecoration(TextDecoration.BOLD, TextDecoration.State.FALSE).setMaxStackSize(64).addPersistentData(ItemUtil.ID, PersistentDataType.STRING, ItemUtil.COW_MILK);

        private int count;

        public Internal() {
            super("ultra_pasteurized");
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            if (shouldPassive(event.getPlayer()) && MATERIALS.contains(event.getBlock().getType()) && MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING)) {
                count++;
            }
            if (count >= MAX) {
                count = 0;
                event.getPlayer().getInventory().addItem(MILK.build());
            }
        }

        @Override
        public void unregister() {
            count = 0;
        }

        @Override
        public Component acbValue() {
            return Type.COMBO_DISABLE.accept(count, MAX, !MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING));
        }
    }
}
