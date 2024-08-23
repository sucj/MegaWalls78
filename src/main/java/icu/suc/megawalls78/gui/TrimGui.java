package icu.suc.megawalls78.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.management.EquipmentManager;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.RandomUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TrimGui {

    private static final int SLOT_COUNT = 21;
    private static final int MIN_PAGE = 1;
    private static final int MAX_PAGE = EquipmentManager.TRIMS.values().size() / SLOT_COUNT + EquipmentManager.TRIMS.values().size() % SLOT_COUNT == 0 ? 0 : 1;
    private static final int[] ID_SLOT = new int[]{
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };
    private static final int PREV_SLOT = 45;
    private static final int RANDOM_SLOT = 49;
    private static final int NEXT_SLOT = 53;

    public static final Map<Inventory, Integer> INVENTORIES = Maps.newHashMap();

    public static void open(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(player, 54, Component.translatable("mw78.gui.trim").append(Component.space()).append(Component.translatable("mw78.gui.trim.title", Component.text(page), Component.text(MAX_PAGE))));

        List<TrimPattern> trims = EquipmentManager.TRIMS.values().stream().skip((page - 1L) * SLOT_COUNT).limit(SLOT_COUNT).toList();
        boolean flag = true;
        for (int i = 0; i < trims.size(); i++) {
            TrimPattern trim = trims.get(i);
            ItemBuilder itemBuilder = itemBuilder(trim)
                    .setDisplayName(trim.description())
                    .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .addItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            if (flag && MegaWalls78.getInstance().getEquipmentManager().getTrim(player.getUniqueId()).equals(trim)) {
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

        inventory.setItem(RANDOM_SLOT, ItemBuilder.of(Material.NETHER_STAR)
                .setDisplayName(Component.translatable("mw78.gui.trim.random"))
                .setNameColor(NamedTextColor.WHITE)
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setEnchantmentGlintOverride(false)
                .build());

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
        ItemStack item = inventory.getItem(slot);
        switch (slot) {
            case PREV_SLOT -> {
                if (item != null) {
                    open(player, INVENTORIES.get(inventory) - 1);
                    INVENTORIES.remove(inventory);
                }
            }
            case RANDOM_SLOT -> {
                List<TrimPattern> trims = Lists.newArrayList(EquipmentManager.TRIMS.values());
                int i = RandomUtil.RANDOM.nextInt(trims.size());
                TrimPattern trim = trims.get(i);
                EquipmentManager equipmentManager = MegaWalls78.getInstance().getEquipmentManager();
                UUID uuid = player.getUniqueId();
                if (equipmentManager.getTrim(uuid).equals(trim)) {
                    if (i == trims.size() - 1) {
                        trim = trims.get(i - 1);
                    } else {
                        trim = trims.get(i + 1);
                    }
                }
                equipmentManager.setTrim(uuid, trim);
                player.sendMessage(Component.translatable("mw78.message.trim.randomly", NamedTextColor.AQUA, trim.description().color(NamedTextColor.WHITE)));

                INVENTORIES.remove(inventory);
                player.closeInventory();
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
                        List<TrimPattern> trims = Lists.newArrayList(EquipmentManager.TRIMS.values());
                        TrimPattern trim = trims.get((INVENTORIES.get(inventory) - 1) * SLOT_COUNT + index);
                        EquipmentManager equipmentManager = MegaWalls78.getInstance().getEquipmentManager();
                        UUID uuid = player.getUniqueId();
                        if (!equipmentManager.getTrim(uuid).equals(trim)) {
                            equipmentManager.setTrim(uuid, trim);
                            player.sendMessage(Component.translatable("mw78.message.trim", NamedTextColor.AQUA, trim.description().color(NamedTextColor.WHITE)));
                            INVENTORIES.remove(inventory);
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }

    public static ItemStack trigger(Player player) {
        TrimPattern trim = MegaWalls78.getInstance().getEquipmentManager().getTrim(player.getUniqueId());
        return itemBuilder(trim)
                .setDisplayName(Component.translatable("mw78.gui.trim", NamedTextColor.WHITE))
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .addItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                .build();
    }

    private static ItemBuilder itemBuilder(TrimPattern trim) {
        return ItemBuilder.of(Material.valueOf(trim.key().value().toUpperCase() + "_ARMOR_TRIM_SMITHING_TEMPLATE"));
    }
}