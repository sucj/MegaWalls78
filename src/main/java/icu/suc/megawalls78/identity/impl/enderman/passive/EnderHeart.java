package icu.suc.megawalls78.identity.impl.enderman.passive;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class EnderHeart extends ChargePassive {

    private static final double KILL = 3.0D;
    private static final double ASSIST = 1.5D;

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> {
        ParticleUtil.spawnParticleRandomBody(player, Particle.PORTAL, 8);
    });

    public EnderHeart() {
        super("ender_heart", 3);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (PASSIVE(player) && CHARGE()) {
            EFFECT_SKILL.play(player);
            List<ItemStack> drops = event.getDrops();
            event.getItemsToKeep().addAll(drops);
            drops.clear();
            CHARGE_RESET();
        }
    }

    @EventHandler
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        GamePlayer player = event.getPlayer();
        if (PASSIVE(player)) {
            Player bukkitPlayer = player.getBukkitPlayer();
            if (bukkitPlayer == null) {
                return;
            }
            bukkitPlayer.heal(KILL);
        }
    }

    @EventHandler
    public void onPlayerAssist(IncreaseStatsEvent.Assist event) {
        GamePlayer player = event.getPlayer();
        if (PASSIVE(player)) {
            Player bukkitPlayer = player.getBukkitPlayer();
            if (bukkitPlayer == null) {
                return;
            }
            player.getBukkitPlayer().heal(ASSIST);
        }
    }
}
