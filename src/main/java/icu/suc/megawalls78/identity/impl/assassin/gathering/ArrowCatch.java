package icu.suc.megawalls78.identity.impl.assassin.gathering;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.impl.assassin.Kit;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.ComponentUtil;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.InventoryUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class ArrowCatch extends Gathering {

    private static final long COOLDOWN = 3000L;

    public ArrowCatch() {
        super("arrow_catch", Internal.class);
    }

    public static final class Internal extends Passive implements IActionbar {

        private long lastMills;

        public Internal() {
            super("arrow_catch");
        }

        @EventHandler
        public void shot(EntityDamageByEntityEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (event.getEntity() instanceof Player player && shouldPassive(player) && event.getDamager() instanceof Arrow arrow) {
                long currentMillis = System.currentTimeMillis();
                if (currentMillis - lastMills >= COOLDOWN) {
                    if (EntityUtil.isEntityInFront(player, arrow)) {
                        lastMills = currentMillis;
                        player.getInventory().addItem(arrow.getItemStack().add(2));
                        arrow.remove();
                        event.setCancelled(true);
                    }
                }
            }
        }

        @Override
        public void unregister() {

        }

        private boolean isAvailable() {
            return !MegaWalls78.getInstance().getGameManager().getRunner().isDm();
        }

        @Override
        public Component acb() {
            return Type.COOLDOWN.accept(System.currentTimeMillis(), lastMills, COOLDOWN);
        }
    }
}
