package icu.suc.mw78.identity.todo.regular.blaze;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import static org.bukkit.Material.*;

public final class Blaze extends Kit {

    public Blaze(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(Material.DIAMOND_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW).addEnchantment(Enchantment.POWER, 2);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        addItem(ARROW, 128);
        setLeggings(IRON_LEGGINGS).addEnchantment(Enchantment.PROTECTION, 3).addEnchantment(Enchantment.BLAST_PROTECTION, 2).addEnchantment(Enchantment.PROJECTILE_PROTECTION, 2);
    }
}
