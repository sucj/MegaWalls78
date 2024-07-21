package icu.suc.megawalls78.identity.impl.enderman.passive;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.ParticleUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnderHeart extends Passive implements IActionbar {

    private static final int MAX = 3;
    private static final double KILL = 3.0D;
    private static final double ASSIST = 1.5D;

    private int count = MAX;

    public EnderHeart() {
        super("ender_heart");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (shouldPassive(player)) {
            if (count++ >= MAX) {
                ParticleUtil.spawnParticleRandomBody(player, Particle.PORTAL, 8);
                List<ItemStack> drops = event.getDrops();
                event.getItemsToKeep().addAll(drops);
                drops.clear();
                count = 1;
            }
        }
    }

    @EventHandler
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        if (shouldPassive(event.getUuid())) {
            Player player = getPlayer().getBukkitPlayer();
            if (shouldPassive(player)) {
                player.heal(KILL);
            }
        }
    }

    @EventHandler
    public void onPlayerAssist(IncreaseStatsEvent.Assist event) {
        if (shouldPassive(event.getUuid())) {
            Player player = getPlayer().getBukkitPlayer();
            if (shouldPassive(player)) {
                player.heal(ASSIST);
            }
        }
    }

    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return Type.COMBO.accept(count, MAX);
    }
}
