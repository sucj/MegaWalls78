package icu.suc.mw78.identity.regular.zombie;

import icu.suc.megawalls78.identity.Identity;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.PROTECTION;

public final class Zombie extends icu.suc.megawalls78.identity.Kit {

    public Zombie(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setHelmet(IRON_HELMET).addEnchantment(PROTECTION, 1);
        setChestplate(DIAMOND_CHESTPLATE).addEnchantment(PROTECTION, 3);
    }
}
