package icu.suc.mw78.identity.mythic.assassin.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Trait(value = "arrow_catch", internal = ArrowCatch.Internal.class)
public final class ArrowCatch extends Gathering {

    @Trait(cooldown = 3000L)
    public static final class Internal extends CooldownPassive {

        @EventHandler(ignoreCancelled = true)
        public void shot(EntityDamageByEntityEvent event) {
            if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && event.getDamager() instanceof Arrow arrow && EntityUtil.isEntityInFront(player, arrow)) {
                player.getInventory().addItem(arrow.getItemStack().add(2));
                arrow.remove();
                event.setCancelled(true);
                COOLDOWN_RESET();
            }
        }
    }
}
