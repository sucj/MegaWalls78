package icu.suc.megawalls78.identity.impl.herobrine;

import icu.suc.megawalls78.identity.Identity;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;
import static org.bukkit.potion.PotionEffectType.*;

public final class Kit extends icu.suc.megawalls78.identity.Kit {

    public Kit(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(DIAMOND_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addSpeedPotion(2, 15, 2);
        addHealingPotion(2, 2).addCustomEffect(new PotionEffect(REGENERATION, 0, 4), false);
        addCompass();
        setHelmet(IRON_HELMET).addEnchantment(AQUA_AFFINITY, 1, true).addEnchantment(PROTECTION, 2, true);
    }
}
