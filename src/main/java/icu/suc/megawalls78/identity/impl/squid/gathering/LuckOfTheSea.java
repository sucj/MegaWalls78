package icu.suc.megawalls78.identity.impl.squid.gathering;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.RandomUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class LuckOfTheSea extends Gathering {

    private static final ItemBuilder POTION = ItemBuilder.of(Material.POTION)
            .setDisplayName(Component.translatable("item.minecraft.potion"))
            .setAmount(3)
            .addPrefix(Identity.SQUID.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMaxStackSize(64)
            .addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 1), false);

    public LuckOfTheSea() {
        super("luck_of_the_sea", Internal.class);
    }

    public static final class Internal extends ChargePassive {

        public Internal() {
            super("luck_of_the_sea", 5);
        }

        @EventHandler
        public void onChestRoll(ChestRollEvent.Post event) {
            if (PASSIVE(event.getPlayer()) && CHARGE()) {
                handle(event);
                CHARGE_RESET();
            }
        }

        private static void handle(ChestRollEvent.Post event) {
            List<Integer> slots = Lists.newArrayList();
            int i = 0;
            Inventory inventory = event.getInventory();
            for (ItemStack itemStack : inventory) {
                if (itemStack == null || itemStack.isEmpty()) {
                    slots.add(i);
                }
                i++;
            }
            inventory.setItem(slots.get(RandomUtil.RANDOM.nextInt(slots.size())), POTION.build());
        }
    }
}
