package icu.suc.megawalls78.identity.impl.spider;

import icu.suc.megawalls78.identity.Identity;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

public final class Spider extends icu.suc.megawalls78.identity.Kit {

    public Spider(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(DIAMOND_SWORD).addEnchantment(BANE_OF_ARTHROPODS, 4, true);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addTool(IRON_SHOVEL).addEnchantment(EFFICIENCY, 1, true);
        addCompass();
        setBoots(DIAMOND_BOOTS).addEnchantment(PROTECTION, 1, true).addEnchantment(FEATHER_FALLING, 1, true);
    }
}
