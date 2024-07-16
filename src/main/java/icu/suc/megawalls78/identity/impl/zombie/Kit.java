package icu.suc.megawalls78.identity.impl.zombie;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Kit extends icu.suc.megawalls78.identity.Kit {

    public Kit(Identity identity) {
        super(identity);
    }

    @Override
    protected void init() {
        addBuilder(ItemBuilder.of(Material.IRON_SWORD)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setUnbreakable(true));
        addBuilder(ItemBuilder.of(Material.ENDER_CHEST)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setMaxStackSize(1)
                .addPersistentData(ItemUtil.ID, PersistentDataType.STRING, ItemUtil.ENDER_CHEST));
        addBuilder(ItemBuilder.of(Material.DIAMOND_PICKAXE)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setUnbreakable(true)
                .addEnchantment(Enchantment.EFFICIENCY, 3, true));
        addBuilder(ItemBuilder.of(Material.BOW)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setUnbreakable(true));
        addBuilder(ItemBuilder.of(Material.COOKED_BEEF)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setAmount(3)
                .setMaxStackSize(3));
        addBuilder(ItemBuilder.of(Material.POTION)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setAmount(4)
                .setMaxStackSize(4)
                .setDisplayName(Component.translatable("item.minecraft.potion.effect.swiftness"))
                .addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1), false));
        addBuilder(ItemBuilder.of(Material.POTION)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setDisplayName(Component.translatable("item.minecraft.potion.effect.healing"))
                .addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 0, 9), false));
        addBuilder(ItemBuilder.of(Material.COMPASS)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setMaxStackSize(1));
        setHelmetBuilder(ItemBuilder.of(Material.IRON_HELMET)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setUnbreakable(true)
                .addEnchantment(Enchantment.PROTECTION, 1, true));
        setChestplateBuilder(ItemBuilder.of(Material.DIAMOND_CHESTPLATE)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setColor(NamedTextColor.AQUA)
                .addPersistentData(ItemUtil.TAG, PersistentDataType.STRING, ItemUtil.SOUL_B0UND)
                .setEnchantmentGlintOverride(true)
                .setUnbreakable(true)
                .addEnchantment(Enchantment.PROTECTION, 3, true));
    }
}
