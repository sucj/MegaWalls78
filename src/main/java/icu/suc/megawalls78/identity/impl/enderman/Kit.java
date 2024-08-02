package icu.suc.megawalls78.identity.impl.enderman;

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
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setBoots(DIAMOND_BOOTS).addEnchantment(PROJECTILE_PROTECTION, 2, true).addEnchantment(FEATHER_FALLING, 2, true);
    }
}
