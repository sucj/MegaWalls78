package icu.suc.mw78.identity.regular.herobrine;

import icu.suc.megawalls78.identity.Identity;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.AQUA_AFFINITY;
import static org.bukkit.enchantments.Enchantment.PROTECTION;
import static org.bukkit.potion.PotionEffectType.REGENERATION;

public final class Herobrine extends icu.suc.megawalls78.identity.Kit {

    public Herobrine(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(DIAMOND_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addHealingPotion(2, 2).addCustomEffect(new PotionEffect(REGENERATION, 18, 4), false);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setHelmet(IRON_HELMET).addEnchantment(AQUA_AFFINITY, 1).addEnchantment(PROTECTION, 2);
    }
}
