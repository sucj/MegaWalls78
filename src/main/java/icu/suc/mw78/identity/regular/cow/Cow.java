package icu.suc.mw78.identity.regular.cow;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Kit;
import icu.suc.megawalls78.util.ItemUtil;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.PROTECTION;
import static org.bukkit.potion.PotionEffectType.REGENERATION;

public final class Cow extends Kit {

    public Cow(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addTool(IRON_SWORD);
        addEnderChest();
        addPickaxe();
        addTool(BOW);
        addItem(MILK_BUCKET, 3).setMW78Id(ItemUtil.COW_MILK);
        addItem(BREAD, 6);
        addHealingPotion(1, 3).addCustomEffect(new PotionEffect(REGENERATION, 6, 4), false);
        addSpeedPotion(2, 15, 2);
        addCompass();
        setChestplate(DIAMOND_CHESTPLATE).addEnchantment(PROTECTION, 1);
    }
}
