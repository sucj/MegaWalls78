package icu.suc.mw78.identity.todo.next.scatha;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

public final class Scatha extends Kit {

    public Scatha(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addPickaxe().addEnchantment(FORTUNE, 1).addEnchantment(SHARPNESS, 3);
        addEnderChest();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setHelmet(DIAMOND_HELMET).addEnchantment(PROTECTION, 5);
    }
}
