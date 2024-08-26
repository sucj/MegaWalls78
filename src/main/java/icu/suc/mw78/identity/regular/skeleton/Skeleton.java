package icu.suc.mw78.identity.regular.skeleton;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

public final class Skeleton extends Kit {

    public Skeleton(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW).addEnchantment(POWER, 3);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        addTool(IRON_AXE).addEnchantment(EFFICIENCY, 1);
        addItem(ARROW, 64);
        setHelmet(DIAMOND_HELMET).addEnchantment(PROTECTION, 1).addEnchantment(PROJECTILE_PROTECTION, 3);
    }
}
