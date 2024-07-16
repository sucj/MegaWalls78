package icu.suc.megawalls78.identity;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

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

    protected void setHelmetBuilder(ItemBuilder helmetBuilder) {
        this.helmetBuilder = helmetBuilder;
    }

    protected void setChestplateBuilder(ItemBuilder chestplateBuilder) {
        this.chestplateBuilder = chestplateBuilder;
    }

    protected void setLeggingsBuilder(ItemBuilder leggingsBuilder) {
        this.leggingsBuilder = leggingsBuilder;
    }

    protected void setBootsBuilder(ItemBuilder bootsBuilder) {
        this.bootsBuilder = bootsBuilder;
    }

    protected void addBuilder(ItemBuilder builder) {
        itemBuilders.add(builder);
    }

    protected Component prefix() {
        return identity.getName().append(Component.space());
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
