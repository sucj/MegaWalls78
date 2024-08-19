package icu.suc.mw78.identity.regular.golem;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.EFFICIENCY;
import static org.bukkit.enchantments.Enchantment.PROTECTION;

public final class Golem extends Kit {

    public Golem(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(COOKED_BEEF, 3);
        addPotion("healing", 2).addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 240, 2), false);
        addItem(SPLASH_POTION, 2).setDisplayName(Component.translatable("item.minecraft.splash_potion.effect.weakness")).addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 140, 0), false).addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 140, 3), false);
        addCompass();
        addTool(IRON_AXE).addEnchantment(EFFICIENCY, 1, true);
        setChestplate(DIAMOND_CHESTPLATE).addEnchantment(PROTECTION, 1, true);
        setBoots(DIAMOND_BOOTS).addEnchantment(PROTECTION, 1, true);
    }
}
