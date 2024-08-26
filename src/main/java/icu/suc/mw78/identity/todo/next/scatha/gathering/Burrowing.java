package icu.suc.mw78.identity.todo.next.scatha.gathering;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.RandomUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Trait(value = "burrowing", internal = Burrowing.Internal.class)
public final class Burrowing extends Gathering {

    private static final int SCALE = 2;

    public static final class Internal extends Passive {

        @EventHandler
        public void onChestRoll(ChestRollEvent.Post event) {
            if (PASSIVE(event.getPlayer())) {
                handle(event);
            }
        }

        private static void handle(ChestRollEvent.Post event) {
            List<ItemStack> itemStacks = Lists.newArrayList();
            for (ItemStack itemStack : event.getInventory()) {
                if (itemStack == null) {
                    continue;
                }
                if (itemStack.getAmount() < itemStack.getMaxStackSize()) {
                    itemStacks.add(itemStack);
                }
            }
            if (itemStacks.isEmpty()) {
                return;
            }
            ItemStack itemStack = RandomUtil.getRandomEntry(itemStacks);
            if (itemStack == null) {
                return;
            }
            itemStack.setAmount(Math.min(itemStack.getAmount() * SCALE, itemStack.getMaxStackSize()));
        }
    }
}
