package icu.suc.mw78.identity.todo.arcanist;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import static org.bukkit.Material.*;

public class Arcanist extends Kit {

    public Arcanist(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(Material.DIAMOND_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setLeggings(DIAMOND_LEGGINGS).addEnchantment(Enchantment.PROTECTION, 3, true).addEnchantment(Enchantment.BLAST_PROTECTION, 2, true);
    }
}
