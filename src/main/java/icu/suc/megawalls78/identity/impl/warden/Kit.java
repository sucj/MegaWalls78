package icu.suc.megawalls78.identity.impl.warden;

import icu.suc.megawalls78.identity.Identity;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.FIRE_PROTECTION;

public class Kit extends icu.suc.megawalls78.identity.Kit {

    public Kit(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(NETHERITE_AXE);
        addEnderChest();
        addPickaxe();
        addHealingPotion(4, 2);
        addCompass();
        setChestplate(NETHERITE_CHESTPLATE).addEnchantment(FIRE_PROTECTION, 10, true);
    }
}
