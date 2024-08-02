package icu.suc.megawalls78.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtil {

    public static final NamespacedKey ID = new NamespacedKey("mw78", "id");
    public static final NamespacedKey TAG = new NamespacedKey("mw78", "tag");

    /* Id */
    public static final String ENDER_CHEST = "ender_chest";
    public static final String COMPASS = "compass";
    public static final String GRAPPLING_HOOK = "grappling_hook";
    public static final String COW_MILK = "cow_milk";
    public static final String MOLEMAN_COOKIE = "moleman_cookie";
    public static final String MOLEMAN_PIE = "moleman_pie";
    public static final String MOLEMAN_JUNK_APPLE = "moleman_junk_apple";

    /* Tags */
    public static final NamespacedKey SOUL_B0UND = new NamespacedKey("mw78", "soul_bound");
    public static final NamespacedKey GRAPPLING_MAX = new NamespacedKey("mw78", "grappling_max");

    public static boolean isMW78Item(ItemStack itemStack, String id) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        } else {
            return id.equals(itemMeta.getPersistentDataContainer().get(ID, PersistentDataType.STRING));
        }
    }

    public static String getMW78Id(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        } else {
            return itemMeta.getPersistentDataContainer().get(ID, PersistentDataType.STRING);
        }
    }

    public static <P, C> C getMW78Tag(ItemStack itemStack, NamespacedKey tag, PersistentDataType<P, C> type) {
        if (itemStack == null) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        } else {
            PersistentDataContainer tags = itemMeta.getPersistentDataContainer().get(TAG, PersistentDataType.TAG_CONTAINER);
            if (tags == null) {
                return null;
            }
            return tags.get(tag, type);
        }
    }

    public static boolean mw78SoulBound(ItemStack itemStack) {
        Boolean soulBound = getMW78Tag(itemStack, SOUL_B0UND, PersistentDataType.BOOLEAN);
        if (soulBound == null) {
            return false;
        }
        return soulBound;
    }
}
