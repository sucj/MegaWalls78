package icu.suc.mw78.identity.next.warden;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.FIRE_PROTECTION;

public final class Warden extends Kit {

    public Warden(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(NETHERITE_AXE);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(4, 2);
        addCompass();
        setChestplate(NETHERITE_CHESTPLATE).addEnchantment(FIRE_PROTECTION, 4);
    }
}
