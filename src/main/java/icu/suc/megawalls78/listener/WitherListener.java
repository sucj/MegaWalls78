package icu.suc.megawalls78.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameRunner;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.ComponentUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WitherListener implements Listener {

    private static final Map<GameTeam, MutablePair<Long, Boolean>> WITHER_WARNING = Maps.newHashMap();
    private static final Map<UUID, Integer> WITHER_LIVES = Maps.newHashMap();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWitherDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Wither wither && event.getDamageSource().getCausingEntity() instanceof Player player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            GameTeam team = gameManager.getWitherTeam(wither);
            if (team == null || gameManager.getPlayer(player).getTeam().equals(team)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWitherDamagePost(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Wither wither && event.getDamageSource().getCausingEntity() instanceof Player player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            GameTeam team = gameManager.getWitherTeam(wither);

            long currentMillis = System.currentTimeMillis();
            MutablePair<Long, Boolean> pair = WITHER_WARNING.computeIfAbsent(team, t -> MutablePair.of(currentMillis, false));
            if (currentMillis - pair.getLeft() > 1500L) {
                boolean flag = pair.getRight();
                for (GamePlayer gamePlayer : gameManager.getTeamPlayersMap().get(team)) {
                    Player bukkitPlayer = gamePlayer.getBukkitPlayer();
                    if (bukkitPlayer != null) {
                        ComponentUtil.sendTitle(Component.empty(), Component.translatable("mw78.wither.attacked", flag ? NamedTextColor.DARK_RED : NamedTextColor.RED), ComponentUtil.ONE_SEC_TIMES_FADE, bukkitPlayer);
                    }
                }
                pair.setLeft(currentMillis);
                pair.setRight(!flag);
            }

            GamePlayer gamePlayer = gameManager.getPlayer(player);
            gamePlayer.increaseDamageWither(Math.min(wither.getHealth(), event.getFinalDamage()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWitherDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Wither wither) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            GameTeam team = gameManager.getWitherTeam(wither);

            GameRunner runner = gameManager.getRunner();
            Bukkit.getScheduler().runTaskAsynchronously(MegaWalls78.getInstance(), () -> runner.getAllowedBlocks().addAll(runner.getTeamRegion(team)));

            BossBar bossBar = gameManager.getBossBar(team);
            Set<GamePlayer> gamePlayers = gameManager.getTeamPlayersMap().get(team);
            for (GamePlayer gamePlayer : gamePlayers) {
                Player bukkitPlayer = gamePlayer.getBukkitPlayer();
                if (bukkitPlayer == null) {
                    gamePlayer.increaseFinalDeaths();
                } else {
                    ComponentUtil.sendTitle(Component.translatable("mw78.wither.died", NamedTextColor.RED), Component.translatable("mw78.respawn.cant", NamedTextColor.YELLOW), ComponentUtil.DEFAULT_TIMES, bukkitPlayer);
                }
            }
            boolean eliminated = true;
            for (GamePlayer gamePlayer : gamePlayers) {
                if (gamePlayer.getFinalDeaths() == 0) {
                    eliminated = false;
                    break;
                }
            }
            gameManager.setTeamEliminate(team, eliminated);
            for (Player player : Bukkit.getOnlinePlayers()) {
                ComponentUtil.sendMessage(Component.translatable("death.attack.generic", wither.name()).decorate(TextDecoration.BOLD), player);
                for (BossBar activeBossBar : player.activeBossBars()) {
                    if (activeBossBar == bossBar) {
                        player.hideBossBar(bossBar);
                    }
                }
            }
            boolean dm = true;
            for (Wither w : gameManager.getWithers()) {
                if (!w.isDead()) {
                    dm = false;
                    break;
                }
            }

            if (dm) {
                runner.startDm();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWitherTick(ServerTickStartEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        GameState state = gameManager.getState();
        if (!state.equals(GameState.OPENING) && !state.equals(GameState.PREPARING)) {
            for (Wither wither : gameManager.getWithers()) {
                UUID uuid = wither.getUniqueId();
                WITHER_LIVES.put(uuid, WITHER_LIVES.computeIfAbsent(uuid, id -> 0) + 1);
                if (!wither.isDead() && WITHER_LIVES.get(uuid) % 100 == 0) {
                    wither.setHealth(Math.max(wither.getHealth() - 4.0D, 0));
                }
            }
        }
    }
}
