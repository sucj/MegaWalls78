package icu.suc.megawalls78.identity.impl.hunter;

import icu.suc.megawalls78.identity.Identity;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

public final class Hunter extends icu.suc.megawalls78.identity.Kit {

    public Hunter(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD).addEnchantment(SHARPNESS, 1, true);
        addEnderChest();
        addPickaxe();
        addTool(BOW).addEnchantment(POWER, 1, true).addEnchantment(INFINITY, 1, true);
        addItem(COOKED_BEEF, 3);
        addItem(GOLDEN_APPLE, 5);
        addTool(CARROT_ON_A_STICK);
        addCompass();
        addItem(ARROW);
        setHelmet(DIAMOND_HELMET).addEnchantment(PROTECTION, 2, true).addEnchantment(PROJECTILE_PROTECTION, 2, true);
        setBoots(IRON_BOOTS).addEnchantment(PROTECTION, 2, true);
    }
}
