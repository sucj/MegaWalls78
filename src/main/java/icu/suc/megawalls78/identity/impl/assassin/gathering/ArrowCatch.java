package icu.suc.megawalls78.identity.impl.assassin.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class ArrowCatch extends Gathering {

    public ArrowCatch() {
        super("arrow_catch", Internal.class);
    }

    public static final class Internal extends CooldownPassive {

        public Internal() {
            super("arrow_catch", 3000L);
        }

        @EventHandler
        public void shot(EntityDamageByEntityEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && event.getDamager() instanceof Arrow arrow && EntityUtil.isEntityInFront(player, arrow)) {
                player.getInventory().addItem(arrow.getItemStack().add(2));
                arrow.remove();
                event.setCancelled(true);
                COOLDOWN_RESET();
            }
        }
    }
}
