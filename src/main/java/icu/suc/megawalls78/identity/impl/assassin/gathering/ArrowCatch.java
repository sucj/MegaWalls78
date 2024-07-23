package icu.suc.megawalls78.identity.impl.assassin.gathering;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.impl.assassin.Kit;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.ComponentUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public final class ArrowCatch extends Gathering {


    public ArrowCatch() {
        super("arrowcatch", Internal.class);
    }

    public static final class Internal extends Passive {


        public Internal() {
            super("arrowcatch");
        }

        @EventHandler
        public void onTick(ServerTickStartEvent event) {
            for (GamePlayer gp : MegaWalls78.getInstance().getGameManager().getPlayers().values()) {
                if ((gp.getIdentity().getKit() instanceof Kit) && shouldPassive(gp.getBukkitPlayer()) && isAvailable()) {
                    for (Entity e : gp.getBukkitPlayer().getNearbyEntities(1.5, 1.5, 1.5)) {
                        if ((e instanceof Arrow) && (((Arrow) e).getShooter() instanceof Player shooter) && PlayerUtil.isValidAllies(gp.getBukkitPlayer(), shooter)) {
                            e.remove();
                            gp.getBukkitPlayer().getInventory().addItem(new ItemStack(Material.ARROW));
                            ComponentUtil.sendMessage(Component.translatable("mw78.id." + this.getId() + ".message", NamedTextColor.AQUA), gp.getBukkitPlayer());
                        }
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
    }
}
