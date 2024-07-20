package icu.suc.megawalls78.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtil {

    public static final NamespacedKey ID = new NamespacedKey("mw78", "id");
    public static final NamespacedKey TAG = new NamespacedKey("mw78", "tag");

    public static final String COW_MILK = "cow_milk";
    public static final String ENDER_CHEST = "ender_chest";
    public static final String SOUL_B0UND = "soul_bound";

    public static boolean mw78SoulBound(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        } else {
            return SOUL_B0UND.equals(itemMeta.getPersistentDataContainer().get(TAG, PersistentDataType.STRING));
        }
    }

    public static boolean mw78EnderChest(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        } else {
            return ENDER_CHEST.equals(itemMeta.getPersistentDataContainer().get(ID, PersistentDataType.STRING));
        }
    }

    public static boolean mw78CowMilk(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        } else {
            return COW_MILK.equals(itemMeta.getPersistentDataContainer().get(ID, PersistentDataType.STRING));
        }
    }

}
