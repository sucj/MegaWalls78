package icu.suc.megawalls78.util;

import icu.suc.megawalls78.MegaWalls78;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtil {

    public static boolean isSoulBound(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        } else {
            return itemMeta.getPersistentDataContainer().getOrDefault(NamespacedKeys.SOUL_BOUND, PersistentDataType.BOOLEAN, false);
        }
    }

    public static boolean isEnderChest(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        } else {
            return itemMeta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ENDER_CHEST, PersistentDataType.BOOLEAN, false);
        }
    }

    public static class NamespacedKeys {
        public static final NamespacedKey SOUL_BOUND = new NamespacedKey(MegaWalls78.getInstance(), "soul_bound");
        public static final NamespacedKey ENDER_CHEST = new NamespacedKey(MegaWalls78.getInstance(), "ender_chest");
    }
}
