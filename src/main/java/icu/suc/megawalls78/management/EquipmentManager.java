package icu.suc.megawalls78.management;

import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.util.Color;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class EquipmentManager {

    public static void decorate(ItemStack itemStack, GamePlayer gamePlayer) {
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        if (itemStack.getType().equals(Material.SHIELD)) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
            Banner banner = (Banner) blockStateMeta.getBlockState();
            for (int i = 0; i < banner.numberOfPatterns(); i++) {
                banner.removePattern(i);
            }
            banner.addPattern(Color.getPattern(gamePlayer.getTeam().color()));
            blockStateMeta.setBlockState(banner);
            itemStack.setItemMeta(blockStateMeta);
            return;
        }
        if (itemMeta instanceof ArmorMeta armorMeta) {
            TrimMaterial trim = Color.getTrim(gamePlayer.getTeam().color());
            if (trim == null) {
                return;
            }
            armorMeta.setTrim(new ArmorTrim(trim, TrimPattern.SHAPER));
            itemStack.setItemMeta(armorMeta);
        }
    }

    public static void clear(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        if (itemStack.getType().equals(Material.SHIELD)) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
            Banner banner = (Banner) blockStateMeta.getBlockState();
            for (int i = 0; i < banner.numberOfPatterns(); i++) {
                banner.removePattern(i);
            }
            blockStateMeta.setBlockState(banner);
            itemStack.setItemMeta(blockStateMeta);
            return;
        }
        if (itemMeta instanceof ArmorMeta armorMeta) {
            armorMeta.setTrim(null);
            itemStack.setItemMeta(armorMeta);
        }
    }
}
