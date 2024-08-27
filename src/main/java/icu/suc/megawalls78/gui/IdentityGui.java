package icu.suc.megawalls78.gui;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.Identity;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IdentityGui {

    private static final int SLOT_COUNT = 21;
    private static final int MIN_PAGE = 1;
    private static final int MAX_PAGE = Identity.values().length / SLOT_COUNT + Identity.values().length % SLOT_COUNT == 0 ? 0 : 1;
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
        Inventory inventory = Bukkit.createInventory(player, 54, Component.translatable("mw78.gui.identity").appendSpace().append(Component.translatable("mw78.gui.identity.title", Component.text(page), Component.text(MAX_PAGE))));

        List<Identity> identities = Arrays.stream(Identity.values()).skip((page - 1L) * SLOT_COUNT).limit(SLOT_COUNT).toList();

        boolean flag = true;
        for (int i = 0; i < identities.size(); i++) {
            Identity identity = identities.get(i);
            ItemBuilder itemBuilder = ItemBuilder.of(identity.getMaterial())
                    .setDisplayName(identity.getName().color(identity.getColor()))
                    .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .clearAttributes();
            if (flag && MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity().equals(identity)) {
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
                .setDisplayName(Component.translatable("mw78.gui.identity.random"))
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
        GamePlayer gamePlayer = MegaWalls78.getInstance().getGameManager().getPlayer(player);
        ItemStack item = inventory.getItem(slot);
        switch (slot) {
            case PREV_SLOT -> {
                if (item != null) {
                    open(player, INVENTORIES.get(inventory) - 1);
                    INVENTORIES.remove(inventory);
                }
            }
            case RANDOM_SLOT -> {
                Identity[] identities = Identity.values();
                int i = RandomUtil.RANDOM.nextInt(identities.length);
                Identity identity = identities[i];
                if (gamePlayer.getIdentity().equals(identity)) {
                    if (i == identities.length - 1) {
                        identity = identities[i - 1];
                    } else {
                        identity = identities[i + 1];
                    }
                }
                gamePlayer.setIdentity(identity);
                player.sendMessage(Component.translatable("mw78.message.identity.randomly", NamedTextColor.AQUA, identity.getName().color(MegaWalls78.getInstance().getIdentityManager().getIdentityColor(player.getUniqueId(), identity))));
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
                        Identity identity = Identity.values()[(INVENTORIES.get(inventory) - 1) * SLOT_COUNT + index];
                        if (!gamePlayer.getIdentity().equals(identity)) {
                            gamePlayer.setIdentity(identity);
                            player.sendMessage(Component.translatable("mw78.message.identity", NamedTextColor.AQUA, identity.getName().color(MegaWalls78.getInstance().getIdentityManager().getIdentityColor(player.getUniqueId(), identity))));
                            INVENTORIES.remove(inventory);
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }

    public static ItemStack trigger(Player player) {
        return ItemBuilder.of(MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity().getMaterial())
                .setDisplayName(Component.translatable("mw78.gui.identity", NamedTextColor.WHITE).appendSpace().append(Component.translatable("mw78.gui.identity.trigger", NamedTextColor.GRAY, Component.keybind("key.use"))))
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .clearAttributes()
                .setHideToolTip(true)
                .build();
    }
}
