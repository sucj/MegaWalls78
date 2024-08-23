package icu.suc.megawalls78.gui;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.Color;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.RandomUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TeamGui {

    private static final int SLOT_COUNT = 7;
    private static final int MIN_PAGE = 1;
    private static final int MAX_PAGE = MegaWalls78.getInstance().getGameManager().getTeams().size() / SLOT_COUNT + MegaWalls78.getInstance().getGameManager().getTeams().size() % SLOT_COUNT == 0 ? 0 : 1;
    private static final int[] ID_SLOT = new int[]{
            10, 11, 12, 13, 14, 15, 16,
    };
    private static final int PREV_SLOT = 27;
    private static final int RANDOM_SLOT = 31;
    private static final int NEXT_SLOT = 35;

    public static final Map<Inventory, Integer> INVENTORIES = Maps.newHashMap();

    public static void open(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(player, 36, Component.translatable("mw78.gui.team").append(Component.space()).append(Component.translatable("mw78.gui.team.title", Component.text(page), Component.text(MAX_PAGE))));

        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        List<GameTeam> teams = gameManager.getTeams().stream().skip((page - 1L) * SLOT_COUNT).limit(SLOT_COUNT).toList();
        Map<GameTeam, Set<GamePlayer>> playersMap = gameManager.getTeamPlayersMap();

        boolean flag = true;
        for (int i = 0; i < teams.size(); i++) {
            GameTeam team = teams.get(i);
            DyeColor dye = Color.getDye(team.color());
            Material material = dye == null ? Material.NAME_TAG : Material.valueOf(dye.name() + "_WOOL");
            ItemBuilder itemBuilder = ItemBuilder.of(material)
                    .setDisplayName(team.name().color(team.color()))
                    .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            int players = playersMap.computeIfAbsent(team, k -> Sets.newHashSet()).size();
            itemBuilder.setAmount(players == 0 ? 1 : players);
            itemBuilder.addLore(Component.translatable("mw78.gui.team.players", NamedTextColor.GRAY,  Component.text(players)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            if (flag && team.equals(MegaWalls78.getInstance().getGameManager().getPlayer(player).getTeam())) {
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
                .setDisplayName(Component.translatable("mw78.gui.team.random"))
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
                MegaWalls78.getInstance().getGameManager().getPlayer(player).setTeam(null);
                player.getInventory().setItem(7, trigger(player));
                player.sendMessage(Component.translatable("mw78.message.team.randomly", NamedTextColor.AQUA));
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
                        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
                        GameTeam team = MegaWalls78.getInstance().getGameManager().getTeams().get((INVENTORIES.get(inventory) - 1) * SLOT_COUNT + index);
                        GamePlayer gamePlayer = gameManager.getPlayer(player);
                        if (!team.equals(gamePlayer.getTeam())) {
                            if (gameManager.getTeamPlayersMap().computeIfAbsent(team, k -> Sets.newHashSet()).size() < Math.ceil((double) gameManager.getPlayers().values().size() / gameManager.getTeams().size())) {
                                gamePlayer.setTeam(team);
                                player.getInventory().setItem(7, trigger(player));
                                player.sendMessage(Component.translatable("mw78.message.team", NamedTextColor.AQUA, team.name().color(team.color())));
                            } else {
                                player.sendMessage(Component.translatable("mw78.message.team.failed", NamedTextColor.RED));
                            }
                            INVENTORIES.remove(inventory);
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }

    public static ItemStack trigger(Player player) {
        GameTeam team = MegaWalls78.getInstance().getGameManager().getPlayer(player).getTeam();
        DyeColor dye = team == null ? null : Color.getDye(team.color());
        Material material = dye == null ? Material.NAME_TAG : Material.valueOf(dye.name() + "_WOOL");
        return ItemBuilder.of(material)
                .setDisplayName(Component.translatable("mw78.gui.team", NamedTextColor.WHITE).append(Component.space()).append(Component.translatable("mw78.gui.team.trigger", NamedTextColor.GRAY, Component.keybind("key.use"))))
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build();
    }
}
