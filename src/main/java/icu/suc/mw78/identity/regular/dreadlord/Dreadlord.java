package icu.suc.mw78.identity.regular.dreadlord;

import icu.suc.megawalls78.identity.Identity;
import org.bukkit.enchantments.Enchantment;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.BLAST_PROTECTION;
import static org.bukkit.enchantments.Enchantment.FIRE_PROTECTION;

public final class Dreadlord extends icu.suc.megawalls78.identity.Kit {

    public Dreadlord(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(DIAMOND_SWORD).addEnchantment(Enchantment.SMITE, 1, true);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setHelmet(DIAMOND_HELMET).addEnchantment(FIRE_PROTECTION, 1, true).addEnchantment(BLAST_PROTECTION, 2, true);
    }
}
