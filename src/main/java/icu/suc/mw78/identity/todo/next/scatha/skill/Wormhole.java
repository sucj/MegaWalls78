package icu.suc.mw78.identity.todo.next.scatha.skill;

import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.ItemUtil;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Wormhole extends Skill {

    private static final int DURATION = 60;

    private static final PotionEffect WEAKNESS = new PotionEffect(PotionEffectType.WEAKNESS, DURATION, 2);

    private Task task;

    public Wormhole() {
        super("wormhole", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        player.addPotionEffect(WEAKNESS);
        task.reset();

        if (run) {
            task.fire();
        }

        return summaryEffectSelf(player, WEAKNESS);
    }

    private static final class Task extends DurationTask {

        public Task(Player player) {
            super(player, DURATION);

            for (ItemStack itemStack : player.getInventory()) {
                if (itemStack == null) {
                    continue;
                }
                if (ItemUtil.mw78SoulBound(itemStack) && Tag.ITEMS_PICKAXES.isTagged(itemStack.getType())) {
                    itemStack.addUnsafeEnchantment(Enchantment.EFFICIENCY, 10);
                }
            }
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            super.run();
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            for (ItemStack itemStack : player.getInventory()) {
                if (itemStack == null) {
                    continue;
                }
                if (ItemUtil.mw78SoulBound(itemStack) && Tag.ITEMS_PICKAXES.isTagged(itemStack.getType())) {
                    itemStack.addUnsafeEnchantment(Enchantment.EFFICIENCY, 3);
                }
            }
            super.cancel();
        }
    }
}
