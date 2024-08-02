package icu.suc.megawalls78.identity.impl.skeleton;

import icu.suc.megawalls78.identity.Identity;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

public final class Kit extends icu.suc.megawalls78.identity.Kit {

    public Kit(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW).addEnchantment(POWER, 3, true);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        addTool(IRON_AXE).addEnchantment(EFFICIENCY, 1, true);
        addItem(ARROW, 64);
        setHelmet(DIAMOND_HELMET).addEnchantment(PROTECTION, 1, true).addEnchantment(PROJECTILE_PROTECTION, 3, true);
    }
}
