package icu.suc.megawalls78.util;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventoryUtil {

    private static final Component ENDERCHEST_TITLE = Component.translatable("container.enderchest");

    public static OptionalInt openEnderChest(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        return serverPlayer.openMenu(
                new SimpleMenuProvider((i, inventory, playerx) -> ChestMenu.threeRows(i, inventory, serverPlayer.getEnderChestInventory()), ENDERCHEST_TITLE)
        );
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
