package icu.suc.mw78.identity.regular.hunter;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

public final class Hunter extends Kit {

    public Hunter(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD).addEnchantment(SHARPNESS, 1);
        addEnderChest();
        addPickaxe();
        addTool(BOW).addEnchantment(POWER, 1).addEnchantment(INFINITY, 1);
        addItem(COOKED_BEEF, 3);
        addItem(GOLDEN_APPLE, 5);
        addTool(CARROT_ON_A_STICK);
        addCompass();
        addItem(ARROW);
        setHelmet(DIAMOND_HELMET).addEnchantment(PROTECTION, 2).addEnchantment(PROJECTILE_PROTECTION, 2);
        setBoots(IRON_BOOTS).addEnchantment(PROTECTION, 2);
    }
}
