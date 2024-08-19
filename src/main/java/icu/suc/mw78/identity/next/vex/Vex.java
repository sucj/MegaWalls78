package icu.suc.mw78.identity.next.vex;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;
import org.bukkit.enchantments.Enchantment;

import static org.bukkit.Material.*;

public final class Vex extends Kit {

    public Vex(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(BREAD, 6);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setChestplate(IRON_CHESTPLATE).addEnchantment(Enchantment.FEATHER_FALLING, 4, true);
    }
}
