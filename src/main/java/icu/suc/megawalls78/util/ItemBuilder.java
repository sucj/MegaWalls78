package icu.suc.megawalls78.util;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ItemBuilder {

    private Material type;
    private Integer amount;

    private Integer maxStackSize;
    private Component displayName;
    private Boolean unbreakable;

    private List<Component> lore;

    private Integer durability;
    private Integer maxDurability;

    private List<PersistentData> persistentDataList;
    private CraftPersistentDataContainer mw78Tags;

    private Boolean enchantmentGlintOverride;
    private List<Enchant> enchantList;

    private List<CustomEffect> customEffectList;

    private Multimap<Attribute, AttributeModifier> attributeMap;

    private List<ItemFlag> itemFlagList;

    private PlayerProfile profile;

    private Color armorColor;

    private List<Component> prefixList;
    private List<Component> suffixList;

    private TextColor nameColor;
    private List<Decoration> decorationList;

    public static ItemBuilder of(Material type) {
        ItemBuilder instance = new ItemBuilder();
        return instance.setType(type);
    }

    public ItemBuilder setType(Material type) {
        this.type = type;
        return this;
    }

    public ItemBuilder setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setMaxStackSize(Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    public ItemBuilder setDisplayName(Component displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder setUnbreakable(Boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemBuilder addLore(Component... lore) {
        if (this.lore == null) {
            this.lore = Lists.newArrayList();
        }
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setDurability(Integer durability) {
        this.durability = durability;
        return this;
    }

    public ItemBuilder setMaxDurability(Integer maxDurability) {
        this.maxDurability = maxDurability;
        return this;
    }

    public <P, C> ItemBuilder addPersistentData(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType, C c) {
        if (this.persistentDataList == null) {
            this.persistentDataList = Lists.newArrayList();
        }
        this.persistentDataList.add(new PersistentData<>(namespacedKey, persistentDataType, c));
        return this;
    }

    public ItemBuilder setMW78Id(String id) {
        return addPersistentData(ItemUtil.ID, PersistentDataType.STRING, id);
    }

    public <P, C> ItemBuilder addMW78Tag(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType, C c) {
        if (this.mw78Tags == null) {
            this.mw78Tags = new CraftPersistentDataContainer(new CraftPersistentDataTypeRegistry());
            addPersistentData(ItemUtil.TAG, PersistentDataType.TAG_CONTAINER, this.mw78Tags);
        }
        this.mw78Tags.set(namespacedKey, persistentDataType, c);
        return this;
     }

    public ItemBuilder setEnchantmentGlintOverride(Boolean enchantmentGlintOverride) {
        this.enchantmentGlintOverride = enchantmentGlintOverride;
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        if (this.enchantList == null) {
            this.enchantList = Lists.newArrayList();
        }
        this.enchantList.add(new Enchant(enchantment, level, ignoreLevelRestriction));
        return this;
    }

    public ItemBuilder addCustomEffect(PotionEffect potionEffect, boolean ambient) {
        if (this.customEffectList == null) {
            this.customEffectList = Lists.newArrayList();
        }
        this.customEffectList.add(new CustomEffect(potionEffect, ambient));
        return this;
    }

    public ItemBuilder addAttribute(Attribute attribute, AttributeModifier attributeModifier) {
        if (this.attributeMap == null) {
            this.attributeMap = HashMultimap.create();
        }
        this.attributeMap.put(attribute, attributeModifier);
        return this;
    }

    public ItemBuilder clearAttributes() {
        if (this.attributeMap == null) {
            this.attributeMap = HashMultimap.create();
        }
        this.attributeMap.clear();
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        if (this.itemFlagList == null) {
            this.itemFlagList = Lists.newArrayList();
        }
        this.itemFlagList.add(itemFlag);
        return this;
    }

    public ItemBuilder setSkullSkin(String value, String signature) {
        if (this.profile == null) {
            this.profile = new CraftPlayerProfile(UUID.randomUUID(), "");
        }
        Set<ProfileProperty> properties = this.profile.getProperties();
        properties.removeIf(property -> property.getName().equals(SkinProperty.TEXTURES_NAME));
        properties.add(new ProfileProperty(SkinProperty.TEXTURES_NAME, value, signature));
        return this;
    }

    public ItemBuilder setArmorColor(Color armorColor) {
        this.armorColor = armorColor;
        return this;
    }

    public ItemBuilder addPrefix(Component prefix) {
        if (this.prefixList == null) {
            this.prefixList = Lists.newArrayList();
        }
        this.prefixList.add(prefix);
        return this;
    }

    public ItemBuilder addSuffix(Component suffix) {
        if (this.suffixList == null) {
            this.suffixList = Lists.newArrayList();
        }
        this.suffixList.add(suffix);
        return this;
    }

    public ItemBuilder setNameColor(TextColor nameColor) {
        this.nameColor = nameColor;
        return this;
    }

    public ItemBuilder addDecoration(TextDecoration decoration, TextDecoration.State state) {
        if (this.decorationList == null) {
            this.decorationList = Lists.newArrayList();
        }
        this.decorationList.add(new Decoration(decoration, state));
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack = ItemStack.of(type);
        if (amount != null) {
            itemStack.setAmount(amount);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (maxStackSize != null) {
            itemMeta.setMaxStackSize(maxStackSize);
        }
        if (displayName != null) {
            itemMeta.displayName(displayName);
        }
        if (unbreakable != null) {
            itemMeta.setUnbreakable(unbreakable);
        }

        itemMeta.lore(lore);

        if (itemMeta instanceof Damageable damageable) {
            if (durability != null) {
                damageable.setDamage(durability);
            }
            damageable.setMaxDamage(maxDurability);
        }

        if (persistentDataList != null) {
            for (PersistentData persistentData : persistentDataList) {
                itemMeta.getPersistentDataContainer().set(persistentData.namespacedKey(), persistentData.persistentDataType(), persistentData.c());
            }
        }

        if (enchantmentGlintOverride != null) {
            itemMeta.setEnchantmentGlintOverride(enchantmentGlintOverride);
        }
        if (enchantList != null) {
            for (Enchant enchant : enchantList) {
                itemMeta.addEnchant(enchant.enchantment(), enchant.level, enchant.ignoreRestrictions);
            }
        }

        if (customEffectList != null) {
            for (CustomEffect customEffect : customEffectList) {
                ((PotionMeta) itemMeta).addCustomEffect(customEffect.potionEffect(), customEffect.ambient());
            }
        }

        if (attributeMap != null) {
            itemMeta.setAttributeModifiers(attributeMap);
        }

        if (itemFlagList != null) {
            for (ItemFlag itemFlag : itemFlagList) {
                if (itemFlag.equals(ItemFlag.HIDE_ATTRIBUTES) && attributeMap == null) {
                    itemMeta.setAttributeModifiers(type.getDefaultAttributeModifiers());
                }
                itemMeta.addItemFlags(itemFlag);
            }
        }

        if (profile != null) {
            try {
                ((SkullMeta) itemMeta).setPlayerProfile(profile);
            } catch (ClassCastException ignored) {}
        }

        if (armorColor != null) {
            try {
                ((ColorableArmorMeta) itemMeta).setColor(armorColor);
            } catch (ClassCastException ignored) {}
        }

        if (prefixList != null) {
            Component prefixes = Component.empty();
            for (Component prefix : prefixList) {
                prefixes = prefixes.append(prefix);
            }
            itemMeta.displayName(prefixes.append(displayName == null ? Component.translatable(itemStack) : displayName));
        }
        if (suffixList != null) {
            Component suffixes = Component.empty();
            for (Component suffix : suffixList) {
                suffixes = suffixes.append(suffix);
            }
            itemMeta.displayName((displayName == null ? Component.translatable(itemStack) : displayName).append(suffixes));
        }

        Component component = itemMeta.displayName();
        if (component == null) {
            if (nameColor != null || decorationList != null) {
                component = Component.translatable(itemStack);
                if (nameColor != null) {
                    component = component.color(nameColor);
                }
                if (decorationList != null) {
                    for (Decoration decoration : decorationList) {
                        component = component.decoration(decoration.decoration(), decoration.state());
                    }
                }
                itemMeta.displayName(component);
            }
        } else {
            if (nameColor != null) {
                component = component.color(nameColor);
            }
            if (decorationList != null) {
                for (Decoration decoration : decorationList) {
                    component = component.decoration(decoration.decoration(), decoration.state());
                }
            }
            itemMeta.displayName(component);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private record PersistentData<P, C>(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType, C c) {
    }

    private record Enchant(Enchantment enchantment, int level, boolean ignoreRestrictions) {
    }

    private record CustomEffect(PotionEffect potionEffect, boolean ambient) {
    }

    private record Decoration(TextDecoration decoration, TextDecoration.State state) {
    }
}
