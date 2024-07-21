package icu.suc.megawalls78.gui;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Skin;
import icu.suc.megawalls78.management.SkinManager;
import icu.suc.megawalls78.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SkinGui {

    private static final int SLOT_COUNT = 21;
    private static final int MIN_PAGE = 1;
    private static final int[] ID_SLOT = new int[]{
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };
    private static final int PREV_SLOT = 45;
    private static final int RESET_SLOT = 49;
    private static final int NEXT_SLOT = 53;

    public static final Map<Inventory, Integer> INVENTORIES = Maps.newHashMap();

    public static void open(Player player, int page) {
        Identity identity = MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity();
        List<Skin> skins = MegaWalls78.getInstance().getSkinManager().getSkins(identity);
        int MAX_PAGE = skins.size() / SLOT_COUNT + 1;

        Inventory inventory = Bukkit.createInventory(player, 54, Component.translatable("mw78.gui.skin").append(Component.space()).append(Component.translatable("mw78.gui.skin.title", Component.text(page), Component.text(MAX_PAGE))));

        Skin selectedSkin = MegaWalls78.getInstance().getSkinManager().getPlayerSelectedSkin(player, identity);
        List<Skin> skinList = skins.stream().skip((page - 1L) * SLOT_COUNT).limit(SLOT_COUNT).toList();

        boolean flag = true;
        for (int i = 0; i < skinList.size(); i++) {
            Skin skin = skinList.get(i);
            ItemBuilder itemBuilder = ItemBuilder.of(Material.PLAYER_HEAD)
                    .setDisplayName(skin.name().color(NamedTextColor.WHITE))
                    .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .setSkullSkin(skin.value(), skin.signature());
            if (flag && selectedSkin.equals(skin)) {
                itemBuilder.addSuffix(Component.space().append(Component.translatable("mw78.gui.selected", NamedTextColor.GRAY))).setEnchantmentGlintOverride(true);
                flag = false;
            }
            inventory.setItem(ID_SLOT[i], itemBuilder.build());
        }

        if (page > MIN_PAGE) {
            inventory.setItem(PREV_SLOT, ItemBuilder.of(Material.ARROW)
                    .setDisplayName(Component.translatable("mw78.gui.prev"))
                    .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
        }

        if (!selectedSkin.equals(skins.getFirst())) {
            inventory.setItem(RESET_SLOT, ItemBuilder.of(Material.REDSTONE_BLOCK)
                    .setDisplayName(Component.translatable("mw78.gui.skin.reset"))
                    .setColor(NamedTextColor.WHITE)
                    .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
        }

        if (page < MAX_PAGE) {
            inventory.setItem(NEXT_SLOT, ItemBuilder.of(Material.ARROW)
                    .setDisplayName(Component.translatable("mw78.gui.next"))
                    .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
        }

        INVENTORIES.put(inventory, page);
        player.openInventory(inventory);
    }

    public static void handle(Player player, Inventory inventory, int slot) {
        SkinManager skinManager = MegaWalls78.getInstance().getSkinManager();
        Identity identity = MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity();
        List<Skin> skins = skinManager.getSkins(identity);
        ItemStack item = inventory.getItem(slot);
        switch (slot) {
            case PREV_SLOT -> {
                if (item != null) {
                    open(player, INVENTORIES.get(inventory) - 1);
                    INVENTORIES.remove(inventory);
                }
            }
            case RESET_SLOT -> {
                if (item != null) {
                    Skin skin = skins.getFirst();
                    skinManager.setPlayerSelectedSkin(player, identity, skin);
                    player.sendMessage(Component.translatable("mw78.skin.reset", NamedTextColor.GREEN, skin.name().color(NamedTextColor.GRAY)));
                    INVENTORIES.remove(inventory);
                    player.closeInventory();
                }
            }
            case NEXT_SLOT -> {
                if (item != null) {
                    open(player, INVENTORIES.get(inventory) + 1);
                    INVENTORIES.remove(inventory);
                }
            }
            default -> {
                if (item != null) {
                    int index = Arrays.binarySearch(ID_SLOT, slot);
                    if (index >= 0) {
                        Skin skin = skins.get((INVENTORIES.get(inventory) - 1) * SLOT_COUNT + index);
                        if (!skinManager.getPlayerSelectedSkin(player, identity).equals(skin)) {
                            skinManager.setPlayerSelectedSkin(player, identity, skin);
                            player.sendMessage(Component.translatable("mw78.skin", NamedTextColor.GREEN, skin.name().color(NamedTextColor.GRAY)));
                            INVENTORIES.remove(inventory);
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }

    public static ItemStack trigger(Player player) {
        Skin skin = MegaWalls78.getInstance().getSkinManager().getPlayerSelectedSkin(player, MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity());
        return ItemBuilder.of(Material.PLAYER_HEAD)
                .setDisplayName(Component.translatable("mw78.gui.skin", NamedTextColor.WHITE).append(Component.space()).append(Component.translatable("mw78.gui.skin.trigger", NamedTextColor.GRAY, Component.keybind("key.use"))))
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setSkullSkin(skin.value(), skin.signature())
                .build();
    }
}
