package icu.suc.megawalls78.gui;

import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.management.TriggerManager;
import icu.suc.megawalls78.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public class TriggerGui { // TODO k

    private static final Component TRUE = Component.translatable("mw78.gui.trigger.true", NamedTextColor.GREEN);
    private static final Component FALSE = Component.translatable("mw78.gui.trigger.false", NamedTextColor.RED);

    private static final int SWORD_ICON_SLOT = 0;
    private static final int SWORD_SET_SLOT = 9;
    private static final int BOW_ICON_SLOT = 1;
    private static final int BOW_SET_SLOT = 10;
    private static final int CROSSBOW_ICON_SLOT = 2;
    private static final int CROSSBOW_SET_SLOT = 11;
    private static final int SHOVEL_ICON_SLOT = 3;
    private static final int SHOVEL_SET_SLOT = 12;
    private static final int PICKAXE_ICON_SLOT = 4;
    private static final int PICKAXE_SET_SLOT = 13;
    private static final int AXE_ICON_SLOT = 5;
    private static final int AXE_SET_SLOT = 14;
    private static final int HOE_ICON_SLOT = 6;
    private static final int HOE_SET_SLOT = 15;
    private static final int CARROT_ON_A_STICK_ICON_SLOT = 7;
    private static final int CARROT_ON_A_STICK_SET_SLOT = 16;
    private static final int WARPED_FUNGUS_ON_A_STICK_ICON_SLOT = 8;
    private static final int WARPED_FUNGUS_ON_A_STICK_SET_SLOT = 17;

    public static final Set<Inventory> INVENTORIES = Sets.newHashSet();

    public static void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 18, Component.translatable("mw78.gui.trigger"));

        TriggerManager manager = MegaWalls78.getInstance().getTriggerManager();
        UUID uuid = player.getUniqueId();

        inventory.setItem(SWORD_ICON_SLOT, ItemBuilder.of(Material.IRON_SWORD)
                .setDisplayName(Skill.Trigger.SWORD.getName())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());
        inventory.setItem(SWORD_SET_SLOT, flag(manager.sneak(uuid, Skill.Trigger.SWORD)));

        inventory.setItem(BOW_ICON_SLOT, ItemBuilder.of(Material.BOW)
                .setDisplayName(Skill.Trigger.BOW.getName())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());
        inventory.setItem(BOW_SET_SLOT, flag(manager.sneak(uuid, Skill.Trigger.BOW)));

        inventory.setItem(CROSSBOW_ICON_SLOT, ItemBuilder.of(Material.CROSSBOW)
                .setDisplayName(Skill.Trigger.CROSSBOW.getName())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());
        inventory.setItem(CROSSBOW_SET_SLOT, flag(manager.sneak(uuid, Skill.Trigger.CROSSBOW)));

        inventory.setItem(SHOVEL_ICON_SLOT, ItemBuilder.of(Material.IRON_SHOVEL)
                .setDisplayName(Skill.Trigger.SHOVEL.getName())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());
        inventory.setItem(SHOVEL_SET_SLOT, flag(manager.sneak(uuid, Skill.Trigger.SHOVEL)));

        inventory.setItem(PICKAXE_ICON_SLOT, ItemBuilder.of(Material.IRON_PICKAXE)
                .setDisplayName(Skill.Trigger.PICKAXE.getName())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());
        inventory.setItem(PICKAXE_SET_SLOT, flag(manager.sneak(uuid, Skill.Trigger.PICKAXE)));

        inventory.setItem(AXE_ICON_SLOT, ItemBuilder.of(Material.IRON_AXE)
                .setDisplayName(Skill.Trigger.AXE.getName())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());
        inventory.setItem(AXE_SET_SLOT, flag(manager.sneak(uuid, Skill.Trigger.AXE)));

        inventory.setItem(HOE_ICON_SLOT, ItemBuilder.of(Material.IRON_HOE)
                .setDisplayName(Skill.Trigger.HOE.getName())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());
        inventory.setItem(HOE_SET_SLOT, flag(manager.sneak(uuid, Skill.Trigger.HOE)));

        inventory.setItem(CARROT_ON_A_STICK_ICON_SLOT, ItemBuilder.of(Material.CARROT_ON_A_STICK)
                .setDisplayName(Skill.Trigger.CARROT_ON_A_STICK.getName())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());
        inventory.setItem(CARROT_ON_A_STICK_SET_SLOT, flag(manager.sneak(uuid, Skill.Trigger.CARROT_ON_A_STICK)));

        inventory.setItem(WARPED_FUNGUS_ON_A_STICK_ICON_SLOT, ItemBuilder.of(Material.WARPED_FUNGUS_ON_A_STICK)
                .setDisplayName(Skill.Trigger.WARPED_FUNGUS_ON_A_STICK.getName())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());
        inventory.setItem(WARPED_FUNGUS_ON_A_STICK_SET_SLOT, flag(manager.sneak(uuid, Skill.Trigger.WARPED_FUNGUS_ON_A_STICK)));

        INVENTORIES.add(inventory);
        player.openInventory(inventory);
    }

    public static void handle(Player player, int slot) {
        TriggerManager manager = MegaWalls78.getInstance().getTriggerManager();
        switch (slot) {
            case SWORD_SET_SLOT -> toggle(manager, player, Skill.Trigger.SWORD);
            case BOW_SET_SLOT -> toggle(manager, player, Skill.Trigger.BOW);
            case CROSSBOW_SET_SLOT -> toggle(manager, player, Skill.Trigger.CROSSBOW);
            case SHOVEL_SET_SLOT -> toggle(manager, player, Skill.Trigger.SHOVEL);
            case PICKAXE_SET_SLOT -> toggle(manager, player, Skill.Trigger.PICKAXE);
            case AXE_SET_SLOT -> toggle(manager, player, Skill.Trigger.AXE);
            case HOE_SET_SLOT -> toggle(manager, player, Skill.Trigger.HOE);
            case CARROT_ON_A_STICK_SET_SLOT -> toggle(manager, player, Skill.Trigger.CARROT_ON_A_STICK);
            case WARPED_FUNGUS_ON_A_STICK_SET_SLOT -> toggle(manager, player, Skill.Trigger.WARPED_FUNGUS_ON_A_STICK);
        }
    }

    private static void toggle(TriggerManager manager, Player player, Skill.Trigger trigger) {
        manager.toggle(player.getUniqueId(), trigger);
        open(player);
    }

    private static ItemStack flag(boolean sneak) {
        return (sneak ? ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE) : ItemBuilder.of(Material.RED_STAINED_GLASS_PANE))
                .setDisplayName(Component.translatable("mw78.gui.trigger.sneak", sneak ? TRUE : FALSE))
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build();
    }
}
