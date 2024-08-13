package icu.suc.megawalls78.identity.impl.golem.passive;

import icu.suc.megawalls78.identity.trait.passive.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public final class IronConstitution extends Passive {

    public IronConstitution() {
        super("iron_constitution");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player)) {

        }
    }
}
