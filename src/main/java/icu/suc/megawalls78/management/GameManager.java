package icu.suc.megawalls78.management;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.myzelyam.api.vanish.VanishAPI;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.StateChangeEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameRunner;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.game.record.GameMap;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.util.ExpiringValue;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private GameMap map;
    private List<GameTeam> teams;

    private GameState state;

    private final Map<UUID, GamePlayer> players;
    private final Set<UUID> spectators;
    private final Map<UUID, Set<ExpiringValue<UUID>>> assistsMap;

    private final Map<GameTeam, Set<GamePlayer>> teamPlayersMap;
    private final Map<GameTeam, Wither> witherMap;
    private final Map<GameTeam, BossBar> witherBossBars;
    private final Map<GameTeam, Boolean> teamEliminateMap;

    private final GameRunner runner;

    public GameManager() {
        this.state = GameState.WAITING;
        this.players = Maps.newHashMap();
        this.spectators = Sets.newHashSet();
        this.assistsMap = Maps.newHashMap();
        this.teamPlayersMap = Maps.newHashMap();
        this.witherMap = Maps.newHashMap();
        this.witherBossBars = Maps.newHashMap();
        this.teamEliminateMap = Maps.newHashMap();
        this.runner = new GameRunner();
        Bukkit.getScheduler().runTaskTimerAsynchronously(MegaWalls78.getInstance(), runner, 0L, 20L);
    }

    public GamePlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public GamePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public void addPlayer(Player player) {
        players.put(player.getUniqueId(), new GamePlayer(player));
        runner.update();
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        runner.update();
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());
        player.setGameMode(GameMode.ADVENTURE);
        VanishAPI.hidePlayer(player);
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());
        if (VanishAPI.isInvisible(player)) {
            VanishAPI.showPlayer(player);
        }
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    public Set<Player> getSpectators() {
        return spectators.stream().map(Bukkit::getPlayer).collect(Collectors.toSet());
    }

    public boolean inWaiting() {
        return state == GameState.WAITING || state == GameState.COUNTDOWN;
    }

    public boolean inFighting() {
        return state == GameState.OPENING || state == GameState.PREPARING || state == GameState.BUFFING || state == GameState.FIGHTING;
    }

    public Set<Player> getTeammates(Player player) {
        Set<Player> players = Sets.newHashSet();
        for (GamePlayer gamePlayer : teamPlayersMap.get(getPlayer(player).getTeam())) {
            players.add(gamePlayer.getBukkitPlayer());
        }
        return players;
    }

    public BossBar addWither(GameTeam team, Wither wither) {
        witherMap.put(team, wither);
        BossBar bossBar = BossBar.bossBar(Objects.requireNonNull(wither.customName()), 1.0F, BossBar.Color.NAMES.valueOr(team.color().toString(), BossBar.Color.WHITE), BossBar.Overlay.PROGRESS);
        witherBossBars.put(team, bossBar);
        return bossBar;
    }

    public GameTeam getWitherTeam(Wither wither) {
        for (GameTeam team : witherMap.keySet()) {
            if (wither.getUniqueId().equals(witherMap.get(team).getUniqueId())) {
                return team;
            }
        }
        return null;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
        Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> Bukkit.getPluginManager().callEvent(new StateChangeEvent(state)));
    }

    public Map<UUID, GamePlayer> getPlayers() {
        return players;
    }

    public Map<GameTeam, Set<GamePlayer>> getTeamPlayersMap() {
        return teamPlayersMap;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public List<GameTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<GameTeam> teams) {
        this.teams = teams;
    }

    public GameRunner getRunner() {
        return runner;
    }

    public int getWitherHealth(GameTeam team) {
        Wither wither = getWither(team);
        return wither == null ? 0 : (int) wither.getHealth();
    }

    public BossBar getBossBar(GameTeam team) {
        return witherBossBars.get(team);
    }

    public Map<GameTeam, BossBar> getWitherBossBars() {
        return witherBossBars;
    }

    public boolean isWitherDead(GameTeam team) {
        Wither wither = getWither(team);
        return wither != null && wither.isDead();
    }

    public Wither getWither(GameTeam team) {
        return witherMap.get(team);
    }

    public Set<ExpiringValue<UUID>> getAssists(Player player) {
        return assistsMap.computeIfAbsent(player.getUniqueId(), k -> Sets.newHashSet());
    }

    public void addAssist(Player player, Player assist) {
        getAssists(player).add(new ExpiringValue<>(assist.getUniqueId(), 10000L));
    }

    public void saveAssists(Player player, Player killer, boolean wither) {
        Set<ExpiringValue<UUID>> assists = getAssists(player);
        for (ExpiringValue<UUID> value : assists) {
            if (value.getValue().equals(killer.getUniqueId()) || value.isExpired()) {
                continue;
            }
            GamePlayer assist = getPlayer(value.getValue());
            if (wither) {
                assist.increaseFinalAssists();
            } else {
                assist.increaseAssists();
            }
        }
        assists.clear();
    }

    public void setTeamEliminate(GameTeam team, boolean eliminated) {
        teamEliminateMap.put(team, eliminated);
    }

    public boolean isEliminated(GameTeam team) {
        return teamEliminateMap.get(team);
    }

    public int getAlive(GameTeam team) {
        int alive = 0;
        for (GamePlayer gamePlayer : teamPlayersMap.get(team)) {
            if (gamePlayer.getFinalDeaths() == 0) {
                alive++;
            }
        }
        return alive;
    }

    public Collection<Wither> getWithers() {
        return witherMap.values();
    }
}
