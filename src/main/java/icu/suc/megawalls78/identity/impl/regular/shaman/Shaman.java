package icu.suc.megawalls78.identity.impl.regular.shaman;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.FEATHER_FALLING;
import static org.bukkit.enchantments.Enchantment.PROTECTION;

public final class Shaman extends Kit {

    public Shaman(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(DIAMOND_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setBoots(DIAMOND_BOOTS).addEnchantment(PROTECTION, 2, true).addEnchantment(FEATHER_FALLING, 2, true);
    }
}
