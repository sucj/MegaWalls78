package icu.suc.megawalls78.identity.impl.assassin;

import icu.suc.megawalls78.identity.Identity;
import org.bukkit.Material;

import static org.bukkit.enchantments.Enchantment.*;

public final class Kit extends icu.suc.megawalls78.identity.Kit {

    public Kit(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(Material.DIAMOND_SWORD).addEnchantment(SHARPNESS, 1, true);
        addEnderChest();
        addPickaxe();
        addTool(Material.BOW);
        addItem(Material.COOKED_BEEF, 3);
        addAssassinPotion(5);
        addCompass();
        setBoots(Material.IRON_BOOTS).addEnchantment(PROJECTILE_PROTECTION, 2, true).addEnchantment(FEATHER_FALLING, 2, true);
    }
}

