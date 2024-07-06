package icu.suc.megawalls78.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class ItemBuilder {

  private Material type;
  private Integer amount;

  private Integer maxStackSize;
  private Component displayName;
  private Boolean unbreakable;
  private Boolean hideToolTip;

  private List<PersistentData> persistentDataList;

  private Boolean enchantmentGlintOverride;
  private List<Enchant> enchantList;

  private List<CustomEffect> customEffectList;

  private Multimap<Attribute, AttributeModifier> attributeMap;

  private List<ItemFlag> itemFlagList;

  private List<Component> prefixList;
  private List<Component> suffixList;

  private List<TextColor> colorList;
  private List<Decoration> decorationList;

  private ItemBuilder() {

  }

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

  public ItemBuilder setHideToolTip(Boolean hideToolTip) {
    this.hideToolTip = hideToolTip;
    return this;
  }

  public <P, C> ItemBuilder addPersistentData(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType, C c) {
    if (this.persistentDataList == null) {
      this.persistentDataList = Lists.newArrayList();
    }
    this.persistentDataList.add(new PersistentData<>(namespacedKey, persistentDataType, c));
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

  public ItemBuilder addColor(TextColor color) {
    if (this.colorList == null) {
      this.colorList = Lists.newArrayList();
    }
    this.colorList.add(color);
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
    ItemStack itemStack = new ItemStack(type);
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
    if (hideToolTip != null) {
      itemMeta.setHideTooltip(hideToolTip);
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
        switch (itemFlag) {
          case HIDE_ATTRIBUTES -> {
            if (attributeMap == null) {
              itemMeta.setAttributeModifiers(type.getDefaultAttributeModifiers());
            }
          }
        }
        itemMeta.addItemFlags(itemFlag);
      }
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
      if (colorList != null || decorationList != null) {
        component = Component.translatable(itemStack);
        if (colorList != null) {
          for (TextColor color : colorList) {
            component = component.color(color);
          }
        }
        if (decorationList != null) {
          for (Decoration decoration : decorationList) {
            component = component.decoration(decoration.decoration(), decoration.state());
          }
        }
        itemMeta.displayName(component);
      }
    } else {
      if (colorList != null) {
        for (TextColor color : colorList) {
          component = component.color(color);
        }
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
