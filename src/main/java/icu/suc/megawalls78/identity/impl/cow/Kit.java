package icu.suc.megawalls78.identity.impl.cow;

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
        addItem(MILK_BUCKET, 3);
        addItem(BREAD, 6);
        addSpeedPotion(2, 15, 2);
        addHealingPotion(1, 3);
        addCompass();
        setChestplate(DIAMOND_CHESTPLATE).addEnchantment(PROTECTION, 1, true);
    }
}
