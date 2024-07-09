package icu.suc.megawalls78.listener;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.management.GameManager;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class WitherListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void EntityDamageByEntityEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Wither wither && event.getDamageSource().getCausingEntity() instanceof Player player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            GameTeam team = gameManager.getWitherTeam(wither);
            if (team == null || gameManager.getPlayer(player).getTeam().equals(team) || gameManager.getState().equals(GameState.PREPARING)) {
                event.setCancelled(true);
            }
//            gameManager.getBossBar(team).progress((float) (wither.getHealth() / wither.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWitherDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Wither wither) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            GameTeam team = gameManager.getWitherTeam(wither);
            BossBar bossBar = gameManager.getBossBar(team);
            for (GamePlayer gamePlayer : gameManager.getTeamPlayersMap().get(team)) {
                Player bukkitPlayer = gamePlayer.getBukkitPlayer();
                if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
                    gamePlayer.increaseFinalDeaths();
                }
            }
            boolean eliminated = true;
            for (GamePlayer gamePlayer : gameManager.getTeamPlayersMap().get(team)) {
                if (gamePlayer.getFinalDeaths() == 0) {
                    eliminated = false;
                    break;
                }
            }
            gameManager.setTeamEliminate(team, eliminated);
            for (Player player : Bukkit.getOnlinePlayers()) {
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
                gameManager.getRunner().startDm();
            }
        }
    }
}
