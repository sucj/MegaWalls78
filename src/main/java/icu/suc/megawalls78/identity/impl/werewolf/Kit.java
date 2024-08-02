package icu.suc.megawalls78.identity.impl.werewolf;

import icu.suc.megawalls78.identity.Identity;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.Material.*;
import static org.bukkit.potion.PotionEffectType.JUMP_BOOST;
import static org.bukkit.potion.PotionEffectType.REGENERATION;

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
        addItem(COOKED_BEEF, 5);
        addHealingPotion(1, 3).addCustomEffect(new PotionEffect(REGENERATION, 6, 4), false);
        addSpeedPotion(2, 15, 2).addCustomEffect(new PotionEffect(JUMP_BOOST, 300, 4), false);
        addCompass();
        setChestplate(DIAMOND_CHESTPLATE);
    }
}
