package icu.suc.megawalls78.identity.impl.squid.gathering;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
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

    private static final int MAX = 5;
    private static final ItemBuilder POTION = ItemBuilder.of(Material.POTION)
            .setAmount(3)
            .addPrefix(Identity.SQUID.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMaxStackSize(64)
            .addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1), false);

    public LuckOfTheSea() {
        super("luck_of_the_sea", Internal.class);
    }

    public static final class Internal extends Passive implements IActionbar {

        private int count = MAX;

        public Internal() {
            super("luck_of_the_sea");
        }

        @EventHandler
        public void onChestRollPost(ChestRollEvent.Post event) {
            if (shouldPassive(event.getPlayer())) {
                if (++count > MAX) {
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
                    count = 1;
                }
            }
        }

        @Override
        public Component acb() {
            return Type.COMBO.accept(count, MAX);
        }

        @Override
        public void unregister() {

        }
    }
}
