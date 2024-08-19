package icu.suc.mw78.identity.regular.squid;

import icu.suc.megawalls78.identity.Identity;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.DEPTH_STRIDER;
import static org.bukkit.enchantments.Enchantment.PROTECTION;
import static org.bukkit.potion.PotionEffectType.REGENERATION;

public class Squid extends icu.suc.megawalls78.identity.Kit {

    public Squid(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_SALMON, 6);
        addHealingPotion(3, 2).addCustomEffect(new PotionEffect(REGENERATION, 6, 4), false);
        addSpeedPotion(1, 15, 2);
        addCompass();
        setBoots(DIAMOND_BOOTS).addEnchantment(PROTECTION, 3, true).addEnchantment(DEPTH_STRIDER, 2, true);
    }
}
