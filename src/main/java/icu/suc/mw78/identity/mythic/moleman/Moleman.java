package icu.suc.mw78.identity.mythic.moleman;

import icu.suc.megawalls78.identity.Identity;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;
import static org.bukkit.potion.PotionEffectType.REGENERATION;

public final class Moleman extends icu.suc.megawalls78.identity.Kit {

    public Moleman(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(DIAMOND_SHOVEL).addEnchantment(EFFICIENCY, 2).addEnchantment(SHARPNESS, 4);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 3).addCustomEffect(new PotionEffect(REGENERATION, 6, 4), false);
        addCompass();
        setHelmet(GOLDEN_HELMET).addEnchantment(PROTECTION, 1);
        setLeggings(DIAMOND_LEGGINGS).addEnchantment(PROTECTION, 3);
    }
}
