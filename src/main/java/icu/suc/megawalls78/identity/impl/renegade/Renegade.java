package icu.suc.megawalls78.identity.impl.renegade;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.persistence.PersistentDataType;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

public final class Renegade extends icu.suc.megawalls78.identity.Kit {
    public Renegade(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD).addEnchantment(SHARPNESS, 1, true);
        addEnderChest();
        addPickaxe();
        addTool(BOW).addEnchantment(POWER, 1, true);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addTool(FISHING_ROD).setDisplayName(Component.translatable("mw78.item.grappling_hook")).setUnbreakable(false).setMaxDurability(100).setMW78Id(ItemUtil.GRAPPLING_HOOK).addMW78Tag(ItemUtil.GRAPPLING_MAX, PersistentDataType.DOUBLE, 22.0D);
        addCompass();
        addItem(ARROW, 48);
        setBoots(DIAMOND_BOOTS).addEnchantment(PROTECTION, 3, true).addEnchantment(FEATHER_FALLING, 2, true).addEnchantment(PROJECTILE_PROTECTION, 1, true);
    }
}
