package icu.suc.megawalls78.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftEnderChest;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventoryUtil {

    public static final Component ENDERCHEST_TITLE = Component.translatable("container.enderchest");
    public static final Component TEAMCHEST_TITLE = Component.translatable("mw78.gui.team_chest");

    private static final Map<UUID, Inventory> ENDERCHESTS = Maps.newHashMap();

    public static void openEnderChest(Player player) {
        player.openInventory(ENDERCHESTS.computeIfAbsent(player.getUniqueId(), k -> Bukkit.createInventory(null, 54, ENDERCHEST_TITLE)));
    }

    public static List<ItemStack> addItem(Inventory inventory, Collection<ItemStack> itemStacks) {
        List<ItemStack> items = Lists.newArrayList();
        for (ItemStack itemStack : itemStacks) {
            HashMap<Integer, ItemStack> leftover = inventory.addItem(itemStack);
            items.addAll(leftover.values());
        }
        return items;
    }

    public static void addItem(Player player, EntityDeathEvent event, ItemStack itemStack) {
        Map<Integer, ItemStack> invLeftover = player.getInventory().addItem(itemStack);
        List<ItemStack> endLeftOver = addItem(player.getEnderChest(), invLeftover.values());
        event.getDrops().addAll(endLeftOver);
    }

    public static void addItem(Player player, BlockDropItemEvent event, ItemStack itemStack) {
        Map<Integer, ItemStack> invLeftover = player.getInventory().addItem(itemStack);
        List<ItemStack> endLeftOver = addItem(player.getEnderChest(), invLeftover.values());
        for (ItemStack item : endLeftOver) {
            BlockUtil.addDrops(event, item);
        }
    }

    public static boolean addItemRandomSlot(Inventory inventory, ItemStack itemStack) {
        List<Integer> slots = Lists.newArrayList();
        int i = -1;
        for (ItemStack slot : inventory) {
            if (slot == null || slot.isEmpty()) {
                if (i == -1) {
                    i = 0;
                }
                slots.add(i);
            }
            i++;
        }
        if (i == -1) {
            return false;
        }
        int size = slots.size();
        if (size == 0) {
            return false;
        }
        inventory.setItem(slots.get(RandomUtil.RANDOM.nextInt(slots.size())), itemStack);
        return true;
    }
}
