package icu.suc.megawalls78.identity.impl.regular.skeleton.passive;

import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class Salvaging extends Passive {

    private static final int FOOD = 1;

    private static final ItemBuilder ARROW = ItemBuilder.of(Material.ARROW).setAmount(2);

    public Salvaging() {
        super("salvaging");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && condition(event)) {
            player.getInventory().addItem(ARROW.build());
            food(player);
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && EntityUtil.isArrowAttack(event);
    }

    private static void food(Player player) {
        PlayerUtil.increaseFoodLevel(player, FOOD);
    }
}
