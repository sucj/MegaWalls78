package icu.suc.megawalls78.gui;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class CosmeticsGui {

    private static final Component TITLE = Component.translatable("mw78.gui.cosmetic");
    private static final Component TRIGGER = Component.translatable("mw78.gui.cosmetic", NamedTextColor.WHITE).appendSpace().append(Component.translatable("mw78.gui.cosmetic.trigger", NamedTextColor.GRAY, Component.keybind("key.use")));

    private static final int PATTERN_SLOT = 0;
    private static final int SKIN_SLOT = 2;
    private static final int TRIM_SLOT = 4;

    public static final Set<Inventory> INVENTORIES = Sets.newHashSet();

    public static void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, InventoryType.HOPPER, TITLE);

        inventory.setItem(PATTERN_SLOT, PatternGui.trigger(player));
        inventory.setItem(SKIN_SLOT, SkinGui.trigger(player));
        inventory.setItem(TRIM_SLOT, TrimGui.trigger(player));

        INVENTORIES.add(inventory);
        player.openInventory(inventory);
    }

    public static void handle(Player player, int slot) {
        switch (slot) {
            case PATTERN_SLOT -> PatternGui.open(player, 1);
            case SKIN_SLOT -> SkinGui.open(player, 1);
            case TRIM_SLOT -> TrimGui.open(player, 1);
        }
    }

    public static ItemStack trigger() {
        return ItemBuilder.of(Material.ARMOR_STAND)
                .setDisplayName(TRIGGER)
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setHideToolTip(true)
                .build();
    }
}
