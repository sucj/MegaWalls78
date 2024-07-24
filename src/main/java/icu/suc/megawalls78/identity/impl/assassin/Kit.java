package icu.suc.megawalls78.identity.impl.assassin;

import icu.suc.megawalls78.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;
import static org.bukkit.potion.PotionEffectType.REGENERATION;
import static org.bukkit.potion.PotionEffectType.SPEED;

public final class Kit extends icu.suc.megawalls78.identity.Kit {

    public Kit(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(DIAMOND_SWORD).addEnchantment(SHARPNESS, 1, true);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addItem(POTION, 5).setDisplayName(Component.translatable("item.minecraft.potion"))
                .addCustomEffect(new PotionEffect(REGENERATION, 6 * 20, 2), false)
                .addCustomEffect(new PotionEffect(SPEED, 6 * 20, 1), false);
        addCompass();
        setBoots(IRON_BOOTS).addEnchantment(PROJECTILE_PROTECTION, 2, true).addEnchantment(FEATHER_FALLING, 2, true);
    }
}

