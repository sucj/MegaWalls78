package icu.suc.megawalls78.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static icu.suc.megawalls78.util.ItemUtil.NamespacedKeys.*;

public class ItemUtil {

    public static boolean isSoulBound(ItemStack itemStack) {
        return is(itemStack, SOUL_BOUND);
    }

    public static boolean isEnderChest(ItemStack itemStack) {
        return is(itemStack, ENDER_CHEST);
    }

    public static boolean isNoBack(ItemStack itemStack) {
        return is(itemStack, NO_BACK);
    }

    public static boolean isCowMilk(ItemStack itemStack) {
        return is(itemStack, COW_MILK);
    }

    private static boolean is(ItemStack itemStack, NamespacedKey key) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        } else {
            return itemMeta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BOOLEAN, false);
        }
    }

    public static class NamespacedKeys {
        public static final NamespacedKey COW_MILK = new NamespacedKey("mw78", "id.cow_milk");
        public static final NamespacedKey ENDER_CHEST = new NamespacedKey("mw78", "id.ender_chest");
        public static final NamespacedKey SOUL_BOUND = new NamespacedKey("mw78", "tag.soul_bound");
        public static final NamespacedKey NO_BACK = new NamespacedKey("mw78", "tag.no_back");
    }
}
