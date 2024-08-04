package icu.suc.megawalls78.identity.impl.hunter;

import icu.suc.megawalls78.identity.Identity;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.INFINITY;

public final class Kit extends icu.suc.megawalls78.identity.Kit {

    public Kit(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW).addEnchantment(INFINITY, 1, true);
        addItem(COOKED_BEEF, 1);
        addItem(GOLDEN_APPLE, 1);
        addTool(CARROT_ON_A_STICK);
        addCompass();
        setHelmet(IRON_HELMET);
        setBoots(IRON_BOOTS);
    }
}
