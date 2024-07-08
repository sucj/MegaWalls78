package icu.suc.megawalls78.game;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.TeamWither;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.management.ConfigManager;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ComponentUtil;
import icu.suc.megawalls78.util.RandomUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Wither;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameRunner implements Runnable {

    private long timer;
    private boolean dm;
    private long dmTimer;
    private boolean dmC;

    private Set<Block> barriers;

    private Set<Location> protectedBlocks;
    private final Map<GameTeam, Set<Location>> palaces;
    private final Set<Location> spawns;
    private final Set<Location> walls;
    private final Set<Location> region;

    public GameRunner() {
        this.barriers = Sets.newHashSet();

        this.palaces = Maps.newHashMap();
        this.spawns = Sets.newHashSet();
        this.walls = Sets.newHashSet();
        this.region = Sets.newHashSet();
    }

    @Override
    public void run() {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        GameState state = gameManager.getState();
        if (!state.equals(GameState.WAITING) && !state.equals(GameState.ENDING)) {
            if (timer <= 1000L) {
                next(state);
            } else {
                if (dmC) {
                    if (dmTimer <= 0L) {
                        dmC = false;
                        dm = true;
                        //TODO DM message
                        Bukkit.broadcast(Component.text("dm"));
                    } else {
                        dmTimer -= 1000L;
                    }
                } else {
                    timer -= 1000L;
                }
                switch (state) {
                    case COUNTDOWN -> {
                        if (timer == 10000L || timer <= 5000L) {
                            Component seconds = ComponentUtil.second(timer);
                            ComponentUtil.sendMessage(Component.translatable("mw78.start.in", NamedTextColor.AQUA, Component.translatable("mw78.seconds", seconds)), Bukkit.getOnlinePlayers());
                            ComponentUtil.sendTitle(Component.empty(), seconds, ComponentUtil.ONE_SEC_TIMES, Bukkit.getOnlinePlayers());
                        }
                    }
                    case OPENING -> {
                        if (timer == 10000L || timer <= 5000L) {
                            ComponentUtil.sendMessage(Component.translatable("mw78.gates", NamedTextColor.AQUA, Component.translatable("mw78.seconds", ComponentUtil.second(timer))), Bukkit.getOnlinePlayers());
                        }
                    }
                }
            }
        }
    }

    public void update() {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        ConfigManager configManager = MegaWalls78.getInstance().getConfigManager();
        switch (gameManager.getState()) {
            case WAITING -> {
                if (gameManager.getPlayers().size() >= configManager.minPlayer) {
                    Bukkit.getScheduler().runTaskLater(MegaWalls78.getInstance(), () -> {
                        this.timer = configManager.waitingTime;
                        gameManager.setState(GameState.COUNTDOWN);
                        if (timer > 10000L || timer < 10000L && timer > 5000L) {
                            Component seconds = ComponentUtil.second(timer);
                            ComponentUtil.sendMessage(Component.translatable("mw78.start.in", NamedTextColor.AQUA, Component.translatable("mw78.seconds", seconds)), Bukkit.getOnlinePlayers());
                            ComponentUtil.sendTitle(Component.empty(), seconds, ComponentUtil.ONE_SEC_TIMES, Bukkit.getOnlinePlayers());
                        }
                    }, 20L);
                }
            }
            case COUNTDOWN -> {
                if (gameManager.getPlayers().size() < configManager.minPlayer) {
                    gameManager.setState(GameState.WAITING);
                    ComponentUtil.sendMessage(Component.translatable("mw78.start.cancel", NamedTextColor.RED), Bukkit.getOnlinePlayers());
                    ComponentUtil.sendTitle(Component.empty(), Component.translatable("mw78.start.wait", NamedTextColor.RED), ComponentUtil.ONE_SEC_TIMES, Bukkit.getOnlinePlayers());
                }
            }
        }
    }

    private void next(GameState state) {
        MegaWalls78 instance = MegaWalls78.getInstance();
        GameManager gameManager = instance.getGameManager();
        ConfigManager configManager = instance.getConfigManager();
        switch (state) {
            case COUNTDOWN -> {
                distributeTeams();
                gameManager.setState(GameState.OPENING);
                timer = configManager.openingTime;
                Audience audience = Audience.audience(Bukkit.getOnlinePlayers());
                audience.sendMessage(Component.text("--------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD).appendNewline()
                        .append(Component.text("           MEGA WALLS           ", NamedTextColor.WHITE, TextDecoration.BOLD)).appendNewline()
                        .appendNewline()
                        .append(Component.text("   LLLLLL! LLLLLL LLLLLLLL L!   ", NamedTextColor.AQUA, TextDecoration.BOLD)).appendNewline()
                        .append(Component.text("   LLLLLLLLLLLLLLLLLLLLLLLLLL   ", NamedTextColor.AQUA, TextDecoration.BOLD)).appendNewline()
                        .append(Component.text("      LLL L LLLLL LLL LLLL      ", NamedTextColor.AQUA, TextDecoration.BOLD)).appendNewline()
                        .appendNewline()
                        .append(Component.text("--------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD)));
                if (timer > 10000L || timer < 10000L && timer > 5000L) {
                    ComponentUtil.sendMessage(Component.translatable("mw78.gates", Component.translatable("mw78.seconds", NamedTextColor.AQUA, ComponentUtil.second(timer))), audience);
                }
                for (GameTeam team : gameManager.getTeams()) {
                    Bukkit.getScheduler().runTask(instance, () -> {
                        placeTeamGate(team);
                        for (GamePlayer gamePlayer : gameManager.getTeamPlayersMap().get(team)) {
                            gamePlayer.getBukkitPlayer().setHealth(0);
                        }
                    });
                }
                protectBlocks();
            }
            case OPENING -> {
                gameManager.setState(GameState.PREPARING);
                timer = configManager.preparingTime;
                Bukkit.getScheduler().runTask(instance, () -> {
                    for (GameTeam team : gameManager.getTeamPlayersMap().keySet()) {
                        if (gameManager.getTeamPlayersMap().get(team).isEmpty()) {
                            gameManager.getWither(team).setHealth(0);
                        }
                    }
                    destroyTeamGate();
                });
                ComponentUtil.sendMessage(Component.translatable("mw78.prepare", NamedTextColor.RED).decorate(TextDecoration.BOLD), Bukkit.getOnlinePlayers());
            }
            case PREPARING -> {
                gameManager.setState(GameState.BUFFING);
                timer = configManager.buffingTime;
                Bukkit.getScheduler().runTask(instance, () -> {
                    destroyWalls();
                    if (protectedBlocks != null) {
                        protectedBlocks.removeAll(walls);
                        protectedBlocks.removeAll(region);
                    }
                    walls.clear();
                });
            }
            case BUFFING -> {
                gameManager.setState(GameState.FIGHTING);
                timer = configManager.fightingTime;
            }
            case FIGHTING -> {
                gameManager.setState(GameState.ENDING);
            }
        }
    }

    private void protectBlocks() {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        for (GameTeam team : gameManager.getTeams()) {
            Set<Location> palace = Sets.newHashSet();
            palaces.put(team, palace);
            addRegionsBlock(team.palace(), palace);
            addRegionsBlock(team.spawn(), spawns);
        }
        addRegionsBlock(gameManager.getMap().wall(), walls);
        addRegionsBlock(gameManager.getMap().region(), region);
    }

    private void addRegionsBlock(Location[][] regions, Set<Location> locations) {
        for (Location[] region : regions) {
            Location locA = region[0];
            Location locB = region[1];
            int minX = Math.min(locA.getBlockX(), locB.getBlockX());
            int maxX = Math.max(locA.getBlockX(), locB.getBlockX());
            int minY = Math.min(locA.getBlockY(), locB.getBlockY());
            int maxY = Math.max(locA.getBlockY(), locB.getBlockY());
            int minZ = Math.min(locA.getBlockZ(), locB.getBlockZ());
            int maxZ = Math.max(locA.getBlockZ(), locB.getBlockZ());
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        locations.add(new Location(Bukkit.getWorlds().getFirst(), x, y, z));
                    }
                }
            }
        }
    }

    private void distributeTeams() {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        List<GameTeam> teams = gameManager.getTeams();
        GameTeam team = RandomUtil.getRandomEntry(teams);
        int i = teams.indexOf(team);
        for (GamePlayer player : gameManager.getPlayers().values()) {
            team = teams.get(i);
            player.setTeam(team);
            gameManager.getTeamPlayersMap().computeIfAbsent(team, k -> Sets.newHashSet()).add(player);
            i++;
            if (i == teams.size()) {
                i = 0;
            }
        }
        for (GameTeam gameTeam : gameManager.getTeams()) {
            gameManager.getTeamPlayersMap().computeIfAbsent(gameTeam, k -> Sets.newHashSet());
            gameManager.setTeamEliminate(gameTeam, false);
        }
        Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            for (GameTeam gameTeam : gameManager.getTeams()) {
                Team mcTeam = scoreboard.registerNewTeam(gameTeam.id());
                mcTeam.color(gameTeam.color());
                mcTeam.setAllowFriendlyFire(false);
                for (GamePlayer player : gameManager.getTeamPlayersMap().get(gameTeam)) {
                    mcTeam.addPlayer(player.getBukkitPlayer());
                }
                EntityUtil.spawn(gameTeam.wither(), EntityUtil.Type.TEAM_WITHER, entity -> {
                    Wither wither = (Wither) entity;
                    wither.customName((gameTeam.name().append(ComponentUtil.BLANK_COMPONENT).append(Component.translatable("entity.minecraft.wither"))).color(gameTeam.color()));
                    mcTeam.addEntity(wither);
                    ((TeamWither) entity.getHandle()).setBossBar(gameManager.addWither(gameTeam, wither));
                });
            }
        });
    }

    private void placeTeamGate(GameTeam team) {
        for (Location[] region : team.spawn()) {
            double minX = Math.min(region[0].getX(), region[1].getX()) - 1;
            double maxX = Math.max(region[0].getX(), region[1].getX()) + 1;
            double minY = Math.min(region[0].getY(), region[1].getY()) - 1;
            double maxY = Math.max(region[0].getY(), region[1].getY()) + 1;
            double minZ = Math.min(region[0].getZ(), region[1].getZ()) - 1;
            double maxZ = Math.max(region[0].getZ(), region[1].getZ()) + 1;
            for (double y = minY; y <= maxY; y++) {
                for (double x = minX; x <= maxX; x++) {
                    placeBarrierBlock(x, y, minZ);
                    placeBarrierBlock(x, y, maxZ);
                }
                for (double z = minZ; z <= maxZ; z++) {
                    placeBarrierBlock(minX, y, z);
                    placeBarrierBlock(maxX, y, z);
                }
            }
        }
    }

    private void destroyTeamGate() {
        for (Block block : barriers) {
            if (block.getType().equals(Material.BARRIER)) {
                block.setType(Material.AIR);
            }
        }
        barriers = null;
    }

    private void destroyWalls() {
        for (Location location : walls) {
            location.getBlock().setType(Material.AIR);
        }
    }

    private void placeBarrierBlock(double x, double y, double z) {
        Block block = Bukkit.getWorlds().getFirst().getBlockAt((int) x, (int) y, (int) z);
        if (block.getType().isAir()) {
            barriers.add(block);
            block.setType(Material.BARRIER);
        }
    }

    public long getTimer() {
        return dmC ? dmTimer : timer;
    }

    public Set<Location> getProtectedBlocks() {
        if (protectedBlocks == null) {
            protectedBlocks = Sets.newHashSet();
            for (Set<Location> palace : palaces.values()) {
                protectedBlocks.addAll(palace);
            }
            protectedBlocks.addAll(spawns);
            protectedBlocks.addAll(walls);
            protectedBlocks.addAll(region);
        }
        return protectedBlocks;
    }

    public GameTeam inPalace(Location location) {
        for (GameTeam team : palaces.keySet()) {
            Location blockLocation = location.toBlockLocation();
            blockLocation.setPitch(0);
            blockLocation.setYaw(0);
            if (palaces.get(team).contains(blockLocation)) {
                return team;
            }
        }
        return null;
    }

    public void startDm() {
        MegaWalls78.getInstance().getGameManager().setState(GameState.FIGHTING);
        dmTimer = MegaWalls78.getInstance().getConfigManager().dmTime;
        timer -= dmTimer;
        dmC = true;
    }

    public boolean isDm() {
        return dm;
    }

    public boolean isDmC() {
        return dmC;
    }
}
