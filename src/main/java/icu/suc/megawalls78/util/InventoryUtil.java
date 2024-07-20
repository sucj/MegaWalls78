package icu.suc.megawalls78.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.OptionalInt;

public class InventoryUtil {

    private static final Component ENDERCHEST_TITLE = Component.translatable("container.enderchest");

    public static OptionalInt openEnderChest(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        return serverPlayer.openMenu(
                new SimpleMenuProvider((i, inventory, playerx) -> ChestMenu.threeRows(i, inventory, serverPlayer.getEnderChestInventory()), ENDERCHEST_TITLE)
        );
    }

    public static void addItem(Inventory inventory, Collection<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            inventory.addItem(itemStack);
        }
    }
}
