package icu.suc.megawalls78.identity;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public abstract class Kit {

    private final List<ItemBuilder> itemBuilders;
    private ItemBuilder helmetBuilder;
    private ItemBuilder chestplateBuilder;
    private ItemBuilder leggingsBuilder;
    private ItemBuilder bootsBuilder;
    private final Identity identity;

    public Kit(Identity identity) {
        this.itemBuilders = Lists.newArrayList();
        this.identity = identity;
        init();
    }

    protected abstract void init();

    protected Component prefix() {
        return identity.getName().append(Component.space());
    }

    protected ItemBuilder addBuilder(ItemBuilder builder) {
        itemBuilders.add(builder);
        return builder;
    }

    protected ItemBuilder setHelmetBuilder(ItemBuilder helmetBuilder) {
        this.helmetBuilder = helmetBuilder;
        return helmetBuilder;
    }

    protected ItemBuilder setChestplateBuilder(ItemBuilder chestplateBuilder) {
        this.chestplateBuilder = chestplateBuilder;
        return chestplateBuilder;
    }

    protected ItemBuilder setLeggingsBuilder(ItemBuilder leggingsBuilder) {
        this.leggingsBuilder = leggingsBuilder;
        return leggingsBuilder;
    }

    protected ItemBuilder setBootsBuilder(ItemBuilder bootsBuilder) {
        this.bootsBuilder = bootsBuilder;
        return bootsBuilder;
    }

    protected ItemBuilder identityItemBuilder(Material material) {
        return ItemBuilder.of(material)
                .addPrefix(prefix())
                .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .setNameColor(NamedTextColor.AQUA)
                .addMW78Tag(ItemUtil.SOUL_B0UND, PersistentDataType.BOOLEAN, true)
                .setEnchantmentGlintOverride(true);
    }

    protected ItemBuilder addItem(Material material) {
        return addBuilder(identityItemBuilder(material));
    }

    protected ItemBuilder addItem(Material material, int amount) {
        ItemBuilder itemBuilder = addItem(material).setAmount(amount);
        if (material.getMaxStackSize() != amount) {
            itemBuilder.setMaxStackSize(amount);
        }
        return itemBuilder;
    }

    protected ItemBuilder addTool(Material material) {
        return addItem(material).setUnbreakable(true);
    }

    protected ItemBuilder addPickaxe() {
        return addTool(Material.DIAMOND_PICKAXE).addEnchantment(Enchantment.EFFICIENCY, 3, true);
    }

    protected ItemBuilder addEnderChest() {
        return addItem(Material.ENDER_CHEST, 1).setMW78Id(ItemUtil.ENDER_CHEST);
    }

    protected ItemBuilder addPotion(String name) {
        return addPotion(name, 1);
    }

    protected ItemBuilder addPotion(String name, int amount) {
        return addItem(Material.POTION, amount).setDisplayName(Component.translatable("item.minecraft.potion.effect." + name));
    }

    protected ItemBuilder addHealingPotion(int amount, int level) {
        return addPotion("healing", amount).addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 0, level - 1), false);
    }

    protected ItemBuilder addSpeedPotion(int amount, int duration, int level) {
        return addPotion("swiftness", amount).addCustomEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, level - 1), false);
    }

    protected ItemBuilder addCompass() {
        return addItem(Material.COMPASS, 1).setMW78Id(ItemUtil.COMPASS);
    }

    protected ItemBuilder setHelmet(Material material) {
        return setHelmetBuilder(identityItemBuilder(material).setUnbreakable(true));
    }

    protected ItemBuilder setChestplate(Material material) {
        return setChestplateBuilder(identityItemBuilder(material).setUnbreakable(true));
    }

    protected ItemBuilder setLeggings(Material material) {
        return setLeggingsBuilder(identityItemBuilder(material).setUnbreakable(true));
    }

    protected ItemBuilder setBoots(Material material) {
        return setBootsBuilder(identityItemBuilder(material).setUnbreakable(true));
    }

    public void equip(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (ItemBuilder itemBuilder : this.itemBuilders) {
            inventory.addItem(itemBuilder.build());
        }
        if (helmetBuilder != null) {
            inventory.setHelmet(helmetBuilder.build());
        }
        if (chestplateBuilder != null) {
            inventory.setChestplate(chestplateBuilder.build());
        }
        if (leggingsBuilder != null) {
            inventory.setLeggings(leggingsBuilder.build());
        }
        if (bootsBuilder != null) {
            inventory.setBoots(bootsBuilder.build());
        }
    }
}
