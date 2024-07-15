package icu.suc.megawalls78.identity.impl.cow.gathering;

import com.google.common.collect.Sets;
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

public class UltraPasteurized extends Gathering {

    public UltraPasteurized() {
        super("ultra_pasteurized", Internal.class);
    }

    public static class Internal extends Passive implements IActionbar {

        private static final int MAX = 60;
        private static final Set<Material> MATERIALS = Sets.immutableEnumSet(Material.STONE, Material.DEEPSLATE);
        private static final ItemBuilder MILK = ItemBuilder.of(Material.MILK_BUCKET).setAmount(2).addPrefix(Identity.COW.getName().append(Component.space())).addDecoration(TextDecoration.BOLD, TextDecoration.State.FALSE).setMaxStackSize(64).addPersistentData(ItemUtil.NamespacedKeys.NO_BACK, PersistentDataType.BOOLEAN, true).addPersistentData(ItemUtil.NamespacedKeys.COW_MILK, PersistentDataType.BOOLEAN, true);

        private int count;

        public Internal() {
            super("ultra_pasteurized");
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            if (shouldPassive(event.getPlayer()) && MATERIALS.contains(event.getBlock().getType())) {
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
            return Type.COMBO.accept(count, MAX);
        }
    }
}
