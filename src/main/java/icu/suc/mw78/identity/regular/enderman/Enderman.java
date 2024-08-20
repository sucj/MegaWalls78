package icu.suc.mw78.identity.regular.enderman;

import icu.suc.megawalls78.identity.Identity;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.FEATHER_FALLING;
import static org.bukkit.enchantments.Enchantment.PROJECTILE_PROTECTION;

public final class Enderman extends icu.suc.megawalls78.identity.Kit {

    public Enderman(Identity identity) {
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
        setBoots(DIAMOND_BOOTS).addEnchantment(PROJECTILE_PROTECTION, 2).addEnchantment(FEATHER_FALLING, 2);
    }
}
