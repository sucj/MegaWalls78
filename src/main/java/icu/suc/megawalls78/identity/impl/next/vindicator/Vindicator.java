package icu.suc.megawalls78.identity.impl.next.vindicator;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;
import org.bukkit.enchantments.Enchantment;

import static org.bukkit.Material.*;

public final class Vindicator extends Kit {

    public Vindicator(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_AXE).addEnchantment(Enchantment.EFFICIENCY, 3, true).addEnchantment(Enchantment.SHARPNESS, 1, true);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(BREAD, 6);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setLeggings(DIAMOND_LEGGINGS);
    }
}
