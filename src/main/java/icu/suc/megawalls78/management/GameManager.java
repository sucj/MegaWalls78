package icu.suc.megawalls78.management;

import com.google.common.collect.Lists;
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
import icu.suc.megawalls78.util.ComponentUtil;
import icu.suc.megawalls78.util.ExpiringValue;
import icu.suc.megawalls78.util.Redis;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private static final JoinConfiguration FOOTER_JOIN = JoinConfiguration.builder().separator(Component.text("   ")).build();

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
        Bukkit.getScheduler().runTaskTimer(MegaWalls78.getInstance(), runner, 0L, 1L);
    }

    public GamePlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public GamePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public void addPlayer(Player player) {
        players.put(player.getUniqueId(), new GamePlayer(player));
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        GamePlayer gamePlayer = players.get(uuid);
        if (gamePlayer == null) {
            return;
        }
        players.remove(uuid);
        GameTeam team = gamePlayer.getTeam();
        if (team == null) {
            return;
        }
        teamPlayersMap.get(team).remove(gamePlayer);
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

    public boolean isSpectator(UUID uuid) {
        return spectators.contains(uuid);
    }

    public boolean isSpectator(Player player) {
        return isSpectator(player.getUniqueId());
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
            Player bukkitPlayer = gamePlayer.getBukkitPlayer();
            if (bukkitPlayer != null) {
                players.add(bukkitPlayer);
            }
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
        Bukkit.getPluginManager().callEvent(new StateChangeEvent(state));
        if (state.equals(GameState.ENDING)) {
            try (Jedis pub = Redis.get()) {
                pub.publish("mw78", String.join("|", "remove", MegaWalls78.getInstance().getConfigManager().server));
            }
        }
    }

    public Map<UUID, GamePlayer> getPlayers() {
        return players;
    }

    public Map<GameTeam, Set<GamePlayer>> getTeamPlayersMap() {
        for (GameTeam team : teamPlayersMap.keySet()) {
            teamPlayersMap.get(team).removeIf(gamePlayer -> !gamePlayer.getTeam().equals(team));
        }
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
        getAssists(player).add(new ExpiringValue<>(assist.getUniqueId(), 30000L));
    }

    public void saveAssists(Player player, UUID killerId, boolean wither) {
        Set<ExpiringValue<UUID>> assists = getAssists(player);
        for (ExpiringValue<UUID> value : assists) {
            if (value.getValue().equals(killerId) || value.isExpired()) {
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
        if (eliminated) {
            Bukkit.getScheduler().runTaskLater(MegaWalls78.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ComponentUtil.sendMessage(Component.empty(), player);
                    ComponentUtil.sendMessage(Component.translatable("mw78.team.eliminated", team.name().color(team.color())).decorate(TextDecoration.BOLD), player);
                    ComponentUtil.sendMessage(Component.empty(), player);
                }

                int s = teamEliminateMap.size();
                for (GameTeam gameTeam : teamEliminateMap.keySet()) {
                    if (teamEliminateMap.get(gameTeam)) {
                        s--;
                    }
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendPlayerListFooter(MegaWalls78.getInstance().getGameManager().footer().appendNewline().append(Component.text("MC.SUC.ICU", NamedTextColor.AQUA, TextDecoration.BOLD)));
                }
                if (s == 1) {
                    runner.next(GameState.FIGHTING);
                }
            }, 20L);
        }
    }

    public boolean isEliminated(GameTeam team) {
        return teamEliminateMap.getOrDefault(team, true);
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

    public Component footer() {
        List<Component> list = Lists.newArrayList();
        for (Map.Entry<GameTeam, Pair<Integer, Integer>> entry : teamScore()) {
            GameTeam team = entry.getKey();
            Integer score = entry.getValue().getLeft();
            list.add(Component.translatable("mw78.team.score", NamedTextColor.GRAY, team.name().color(team.color()), Component.text(score, NamedTextColor.WHITE)));
        }
        return Component.join(FOOTER_JOIN, list);
    }

    public List<Map.Entry<GameTeam, Pair<Integer, Integer>>> teamScore() {
        List<Map.Entry<GameTeam, Pair<Integer, Integer>>> list = Lists.newArrayList();
        for (GameTeam team : teamPlayersMap.keySet()) {
            if (isEliminated(team)) {
                continue;
            }
            int score = 0;
            int asc = 0;
            for (GamePlayer gamePlayer : teamPlayersMap.get(team)) {
                if (gamePlayer.getFinalDeaths() == 0) {
                    int i = gamePlayer.getFinalKills() * 2;
                    score += i;
                    asc += i;
                } else {
                    score += gamePlayer.getFinalKills();
                }
            }
            list.add(new AbstractMap.SimpleEntry<>(team, Pair.of(score, asc)));
        }
        list.sort((o1, o2) -> {
            Pair<Integer, Integer> p1 = o1.getValue();
            Pair<Integer, Integer> p2 = o2.getValue();

            int cmp = Integer.compare(p2.getLeft(), p1.getLeft());
            if (cmp != 0) {
                return cmp;
            }

            return Integer.compare(p2.getRight(), p1.getRight());
        });
        return list;
    }
}
