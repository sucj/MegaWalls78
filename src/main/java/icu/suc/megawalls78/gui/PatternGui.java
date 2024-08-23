package icu.suc.megawalls78.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.management.EquipmentManager;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.RandomUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class PatternGui {

    private static final int SLOT_COUNT = 21;
    private static final int MIN_PAGE = 1;
    private static final int MAX_PAGE = EquipmentManager.PATTERNS.values().size() / SLOT_COUNT + EquipmentManager.PATTERNS.values().size() % SLOT_COUNT == 0 ? 0 : 1;
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
        Inventory inventory = Bukkit.createInventory(player, 54, Component.translatable("mw78.gui.pattern").append(Component.space()).append(Component.translatable("mw78.gui.pattern.title", Component.text(page), Component.text(MAX_PAGE))));

        List<Pattern> patterns = EquipmentManager.PATTERNS.values().stream().skip((page - 1L) * SLOT_COUNT).limit(SLOT_COUNT).collect(Collectors.toList());
        patterns.addFirst(null);

        boolean flag = true;
        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            ItemBuilder itemBuilder = ItemBuilder.of(Material.WHITE_BANNER)
                    .setDisplayName(name(pattern))
                    .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            if (pattern != null) {
                itemBuilder.addItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP).addBannerPattern(pattern);
            }
            if (flag && Objects.equals(MegaWalls78.getInstance().getEquipmentManager().getPattern(player.getUniqueId()), pattern)) {
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
                .setDisplayName(Component.translatable("mw78.gui.pattern.random"))
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
                List<Pattern> patterns = Lists.newArrayList(EquipmentManager.PATTERNS.values());
                patterns.addFirst(EquipmentManager.PATTERN_NONE);
                int i = RandomUtil.RANDOM.nextInt(patterns.size());
                Pattern pattern = patterns.get(i);
                EquipmentManager equipmentManager = MegaWalls78.getInstance().getEquipmentManager();
                UUID uuid = player.getUniqueId();
                if (Objects.equals(equipmentManager.getPattern(uuid), pattern)) {
                    if (i == patterns.size() - 1) {
                        pattern = patterns.get(i - 1);
                    } else {
                        pattern = patterns.get(i + 1);
                    }
                }
                equipmentManager.setPattern(uuid, pattern);
                player.sendMessage(Component.translatable("mw78.message.pattern.randomly", NamedTextColor.AQUA, name(pattern).color(NamedTextColor.WHITE)));
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
                        List<Pattern> patterns = Lists.newArrayList(EquipmentManager.PATTERNS.values());
                        patterns.addFirst(EquipmentManager.PATTERN_NONE);
                        Pattern pattern = patterns.get((INVENTORIES.get(inventory) - 1) * SLOT_COUNT + index);
                        EquipmentManager equipmentManager = MegaWalls78.getInstance().getEquipmentManager();
                        UUID uuid = player.getUniqueId();
                        if (!Objects.equals(equipmentManager.getPattern(uuid), pattern)) {
                            equipmentManager.setPattern(uuid, pattern);
                            player.sendMessage(Component.translatable("mw78.message.pattern", NamedTextColor.AQUA, name(pattern).color(NamedTextColor.WHITE)));
                            INVENTORIES.remove(inventory);
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }

    public static ItemStack trigger(Player player) {
        ItemBuilder itemBuilder = ItemBuilder.of(Material.WHITE_BANNER)
                .setDisplayName(Component.translatable("mw78.gui.pattern", NamedTextColor.WHITE))
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        Pattern pattern = MegaWalls78.getInstance().getEquipmentManager().getPattern(player.getUniqueId());
        if (pattern != null) {
            itemBuilder.addItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP).addBannerPattern(pattern);
        }
        return itemBuilder.build();
    }

    private static Component name(Pattern pattern) {
        if (pattern == null) {
            return Component.translatable("mw78.gui.pattern.none");
        } else {
            return Component.translatable("item.minecraft." + pattern.getPattern().key().value() + "_banner_pattern.desc");
        }
    }
}
