package icu.suc.megawalls78.management;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.StateChangeEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.util.ComponentUtil;
import icu.suc.megawalls78.util.Formatters;
import icu.suc.megawalls78.util.LP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ScoreboardManager implements Listener {

    public static final SidebarComponent TITLE = SidebarComponent.staticLine(Component.translatable("mw78.sb.title").color(AQUA).decorate(TextDecoration.BOLD));
    public static final SidebarComponent IP = SidebarComponent.staticLine(Component.text("mc.suc.icu").color(AQUA));
    public static final SidebarComponent INFO = SidebarComponent.dynamicLine(() -> Component.text(LocalDate.now().format(Formatters.DATE)).color(GRAY).append(Component.space()).append(Component.text("TEST").color(DARK_GRAY)));

    private final Map<UUID, SidebarWrapper> sidebarMap;
    private final TeamManager teamManager;
    private final ObjectiveManager objectiveManager;
    private final ScoreboardObjective belowName;
    private final ScoreboardObjective playerList;

    public ScoreboardManager() {
        sidebarMap = Maps.newHashMap();
        teamManager = MegaWalls78.getScoreboardLib().createTeamManager();
        objectiveManager = MegaWalls78.getScoreboardLib().createObjectiveManager();
        belowName = objectiveManager.create("below_name");
        belowName.value(Component.translatable("mw78.hp", RED));
        objectiveManager.display(ObjectiveDisplaySlot.belowName(), belowName);
        playerList = objectiveManager.create("player_list");
        objectiveManager.display(ObjectiveDisplaySlot.playerList(), playerList);
    }

    public void updateSidebar(GameState state) {
        for (UUID uuid : sidebarMap.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            sidebarMap.get(uuid).setLayout(getLayout(player, state));
        }
    }

    public void updateSidebar(Player player, GameState state) {
        SidebarWrapper sidebar = sidebarMap.computeIfAbsent(player.getUniqueId(), k -> new SidebarWrapper(player));
        sidebar.setLayout(getLayout(player, state));
    }

    public void removeSidebar(Player player) {
        sidebarMap.get(player.getUniqueId()).close();
        sidebarMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        for (SidebarWrapper sidebar : sidebarMap.values()) {
            sidebar.tick();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            int health = (int) player.getHealth();
            belowName.score(player.getName(), health);

            TextColor color;
            if (health > 20) {
                color = TextColor.lerp((float) (health - 20) / 20, YELLOW, GREEN);
            } else {
                color = TextColor.lerp((float) health / 20, RED, YELLOW);
            }
            playerList.score(player.getName(), health, ScoreFormat.styled(Style.style(color)));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        updateSidebar(player, gameManager.getState());
        teamManager.addPlayer(player);
        if (MegaWalls78.getInstance().getGameManager().inWaiting()) {
            TeamDisplay teamDisplay = teamManager.createIfAbsent(String.valueOf(player.getUniqueId())).defaultDisplay();
            teamDisplay.canSeeFriendlyInvisibles(true);
            teamDisplay.prefix(LP.getPrefix(player.getUniqueId()));
            Identity identity = MegaWalls78.getInstance().getIdentityManager().getRankedIdentity(player.getUniqueId());
            if (identity != null) {
                teamDisplay.suffix(Component.space().append(identity.getIcon().color(MegaWalls78.getInstance().getIdentityManager().getIdentityColor(player.getUniqueId(), identity))));
            }
            teamDisplay.playerColor(LP.getNameColor(player.getUniqueId()));
            teamDisplay.addEntry(player.getName());
        } else {
            GamePlayer gamePlayer = MegaWalls78.getInstance().getGameManager().getPlayer(player);
            TeamDisplay teamDisplay = teamManager.createIfAbsent(String.valueOf(player.getUniqueId())).defaultDisplay();
            teamDisplay.canSeeFriendlyInvisibles(true);
            if (MegaWalls78.getInstance().getGameManager().isSpectator(player)) {
                teamDisplay.prefix(Component.translatable("mw78.brackets", GRAY, Component.translatable("mw78.team.spec.abbr")).append(Component.space()));
                teamDisplay.playerColor(GRAY);
            } else if (gamePlayer != null) {
                teamDisplay.prefix(Component.translatable("mw78.brackets", gamePlayer.getTeam().color(), gamePlayer.getTeam().abbr()).append(Component.space()));
                teamDisplay.suffix(Component.space().append(Component.translatable("mw78.brackets", MegaWalls78.getInstance().getIdentityManager().getIdentityColor(player.getUniqueId(), gamePlayer.getIdentity()), gamePlayer.getIdentity().getAbbr())));
                teamDisplay.playerColor(gamePlayer.getTeam().color());
            }
            objectiveManager.addPlayer(player);
            teamDisplay.addEntry(player.getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeSidebar(player);
        teamManager.removePlayer(player);
        teamManager.removeTeam(String.valueOf(player.getUniqueId()));
        objectiveManager.removePlayer(player);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = MegaWalls78.getInstance().getGameManager().getPlayer(player);
        TeamDisplay teamDisplay = teamManager.createIfAbsent(String.valueOf(player.getUniqueId())).defaultDisplay();
        teamDisplay.canSeeFriendlyInvisibles(true);
        if (MegaWalls78.getInstance().getGameManager().isSpectator(player)) {
            teamDisplay.prefix(Component.translatable("mw78.brackets", GRAY, Component.translatable("mw78.team.spec.abbr")).append(Component.space()));
            teamDisplay.playerColor(GRAY);
        } else {
            teamDisplay.prefix(Component.translatable("mw78.brackets", gamePlayer.getTeam().color(), gamePlayer.getTeam().abbr()).append(Component.space()));
            teamDisplay.suffix(Component.space().append(Component.translatable("mw78.brackets", MegaWalls78.getInstance().getIdentityManager().getIdentityColor(player.getUniqueId(), gamePlayer.getIdentity()), gamePlayer.getIdentity().getAbbr())));
            teamDisplay.playerColor(gamePlayer.getTeam().color());
        }
        objectiveManager.addPlayer(player);
        teamDisplay.addEntry(player.getName());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        teamManager.removeTeam(String.valueOf(player.getUniqueId()));
    }

    @EventHandler
    public void onChange(StateChangeEvent event) {
        updateSidebar(event.getState());
    }

    private static ComponentSidebarLayout getLayout(Player player, GameState state) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        ConfigManager configManager = MegaWalls78.getInstance().getConfigManager();
        IdentityManager identityManager = MegaWalls78.getInstance().getIdentityManager();
        GamePlayer gamePlayer = gameManager.getPlayer(player);
        switch (state) {
            case WAITING -> {
                return new ComponentSidebarLayout(TITLE, SidebarComponent.builder()
                        .addComponent(INFO)
                        .addBlankLine()
                        .addStaticLine(Component.translatable("mw78.sb.map", gameManager.getMap().name().color(AQUA)))
                        .addDynamicLine(() -> Component.translatable("mw78.sb.players", Component.translatable("mw78.sb.online", Component.text(gameManager.getPlayers().size()), Component.text(configManager.maxPlayer)).color(AQUA)))
                        .addBlankLine()
                        .addStaticLine(Component.translatable("mw78.sb.start.0", Component.text(configManager.minPlayer).color(AQUA)))
                        .addStaticLine(Component.translatable("mw78.sb.start.1"))
                        .addBlankLine()
                        .addStaticLine(Component.translatable("mw78.sb.identity"))
                        .addDynamicLine(() -> gamePlayer.getIdentity().getName().color(AQUA).append(Component.space()).append(gamePlayer.getIdentity().getIcon().color(identityManager.getIdentityColor(player.getUniqueId(), gamePlayer.getIdentity()))))
                        .addBlankLine()
                        .addComponent(IP)
                        .build());
            }
            case COUNTDOWN -> {
                return new ComponentSidebarLayout(TITLE, SidebarComponent.builder()
                        .addComponent(INFO)
                        .addBlankLine()
                        .addStaticLine(Component.translatable("mw78.sb.map", gameManager.getMap().name().color(AQUA)))
                        .addDynamicLine(() -> Component.translatable("mw78.sb.players", Component.translatable("mw78.sb.online", Component.text(gameManager.getPlayers().size()), Component.text(configManager.maxPlayer)).color(AQUA)))
                        .addBlankLine()
                        .addDynamicLine(() -> Component.translatable("mw78.sb.start", Component.translatable("mw78.seconds", ComponentUtil.second(gameManager.getRunner().getTimer()))))
                        .addBlankLine()
                        .addStaticLine(Component.translatable("mw78.sb.identity"))
                        .addDynamicLine(() -> gamePlayer.getIdentity().getName().color(AQUA).append(Component.space()).append(gamePlayer.getIdentity().getIcon().color(identityManager.getIdentityColor(player.getUniqueId(), gamePlayer.getIdentity()))))
                        .addBlankLine()
                        .addComponent(IP)
                        .build());
            }
            case OPENING -> {
                return new ComponentSidebarLayout(TITLE, fightingSidebar("mw78.sb.gate", gameManager, gamePlayer));
            }
            case PREPARING -> {
                return new ComponentSidebarLayout(TITLE, fightingSidebar("mw78.sb.fall", gameManager, gamePlayer));
            }
            case BUFFING -> {
                return new ComponentSidebarLayout(TITLE, fightingSidebar("mw78.sb.energy", gameManager, gamePlayer));
            }
            case FIGHTING -> {
                if (gameManager.getRunner().isDmC()) {
                    return new ComponentSidebarLayout(TITLE, fightingSidebar("mw78.sb.dm", gameManager, gamePlayer));
                } else {
                    return new ComponentSidebarLayout(TITLE, fightingSidebar("mw78.sb.end", gameManager, gamePlayer));
                }
            }
            case ENDING -> {
                return new ComponentSidebarLayout(TITLE, fightingSidebar("mw78.sb.end", gameManager, gamePlayer));
            }
        }
        return null;
    }

    private static SidebarComponent fightingSidebar(String name, GameManager gameManager, GamePlayer gamePlayer) {
        SidebarComponent.Builder builder = SidebarComponent.builder()
                .addComponent(INFO)
                .addBlankLine()
                .addDynamicLine(() -> Component.translatable(name, ComponentUtil.mmss(gameManager.getRunner().getTimer())))
                .addBlankLine();
        for (GameTeam team : gameManager.getTeamPlayersMap().keySet()) {
            builder.addDynamicLine(() -> {
                if (gameManager.isEliminated(team)) {
                    return Component.translatable("mw78.sb.team.eliminated", GRAY, team.name());
                } else {
                    Component component = Component.translatable("mw78.brackets", team.color(), team.abbr()).append(Component.space());
                    Component label;
                    Component value;
                    if (gameManager.isWitherDead(team)) {
                        label = Component.translatable("mw78.sb.team.players");
                        value = Component.text(gameManager.getAlive(team));
                    } else {
                        label = Component.translatable("mw78.sb.team.wither");
                        value = Component.text(gameManager.getWitherHealth(team));
                    }
                    if (gamePlayer != null && team.equals(gamePlayer.getTeam())) {
                        label = label.color(team.color());
                    } else {
                        label = label.color(WHITE);
                    }
                    component = component.append(label).append(Component.translatable("mw78.sb.colon", WHITE)).append(value.color(team.color()));
                    return component;
                }
            });
        }
        builder.addBlankLine();
        if (gamePlayer != null) {
            builder.addDynamicLine(() -> Component.translatable("mw78.sb.ka", Component.text(gamePlayer.getKills()).color(AQUA), Component.text(gamePlayer.getAssists()).color(AQUA)))
                    .addDynamicLine(() -> Component.translatable("mw78.sb.fka", Component.text(gamePlayer.getFinalKills()).color(AQUA), Component.text(gamePlayer.getFinalAssists()).color(AQUA)))
                    .addBlankLine();
        }
        builder.addComponent(IP);
        return builder.build();
    }

    private static class SidebarWrapper {

        private final Sidebar sidebar;
        private ComponentSidebarLayout layout;

        public SidebarWrapper(Player player) {
            this.sidebar = MegaWalls78.getScoreboardLib().createSidebar();
            this.sidebar.addPlayer(player);
        }

        public void tick() {
            if (layout == null) {
                return;
            }
            layout.apply(sidebar);
        }

        public void setLayout(ComponentSidebarLayout layout) {
            this.layout = layout;
        }

        public void close() {
            sidebar.close();
        }
    }
}
