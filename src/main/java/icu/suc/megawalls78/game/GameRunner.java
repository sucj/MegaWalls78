package icu.suc.megawalls78.game;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.TeamWither;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.EnergyWay;
import icu.suc.megawalls78.management.ConfigManager;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ComponentUtil;
import icu.suc.megawalls78.util.RandomUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameRunner implements Runnable {

    private static final Set<Location> EMPTY = Set.of();

    private long tick;
    private long timer;
    private boolean dm;
    private long dmTimer;
    private boolean dmC;

    private boolean init;
    private boolean initializing;

    private Set<Block> barriers;
    private final Set<Location> allowedBlocks;
    private final Map<GameTeam, Set<Location>> palaces;
    private final Map<GameTeam, Set<Location>> spawns;
    private final Set<Location> walls;
    private final Set<Location> region;
    private final Map<GameTeam, Set<Location>> regions;

    private final Set<BoundingBox> mid;
    private final PotionEffect HUNGER = new PotionEffect(PotionEffectType.HUNGER, 20, 1, true, false);

    public GameRunner() {
        this.barriers = Sets.newHashSet();
        this.allowedBlocks = Sets.newConcurrentHashSet();
        this.palaces = Maps.newHashMap();
        this.spawns = Maps.newHashMap();
        this.walls = Sets.newHashSet();
        this.region = Sets.newHashSet();
        this.regions = Maps.newHashMap();
        this.mid = Sets.newHashSet();
    }

    private void init(GameManager gameManager) {
        initializing = true;
        Bukkit.getScheduler().runTaskAsynchronously(MegaWalls78.getInstance(), () -> {
            MegaWalls78.getInstance().getLogger().info("Map initializing...");
            for (GameTeam team : gameManager.getTeams()) {
                Set<Location> palace = Sets.newHashSet();
                palaces.put(team, palace);
                addRegionsBlock(team.palace(), palace);
                Set<Location> spawn = Sets.newHashSet();
                spawns.put(team, spawn);
                addRegionsBlock(team.spawn(), spawn);
                Set<Location> region = Sets.newHashSet();
                regions.put(team, region);
                addRegionsBlock(team.region(), region);
            }
            addRegionsBlock(gameManager.getMap().wall(), walls);
            Location[][] midRegion = gameManager.getMap().region();
            addRegionsBlock(midRegion, region);
            for (Location[] locations : midRegion) {
                Location locA = locations[0];
                Location locB = locations[1];
                mid.add(new BoundingBox(locA.getX(), locA.getY(), locA.getZ(), locB.getX(), locB.getY(), locB.getZ()));
            }
            init = true;
            MegaWalls78.getInstance().getLogger().info("Map initialized.");
        });
    }

    @Override
    public void run() {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        if (init) {
            ConfigManager configManager = MegaWalls78.getInstance().getConfigManager();
            GameState state = gameManager.getState();
            boolean flag = true;
            switch (state) {
                case WAITING -> {
                    if (gameManager.getPlayers().size() >= configManager.minPlayer) {
                        tick = 1;
                        flag = false;
                        this.timer = configManager.waitingTime;
                        gameManager.setState(GameState.COUNTDOWN);
                        if (timer >= 10000L || timer >= 5000L) {
                            MegaWalls78.getInstance().getLogger().info("Game starts in " + timer + " ms.");
                            Component seconds = ComponentUtil.second(timer);
                            ComponentUtil.sendMessage(Component.translatable("mw78.start.in", NamedTextColor.AQUA, Component.translatable("mw78.seconds", seconds)), Bukkit.getOnlinePlayers());
                            ComponentUtil.sendTitle(Component.empty(), seconds, ComponentUtil.ONE_SEC_TIMES, Bukkit.getOnlinePlayers());
                        }
                    }
                }
                case COUNTDOWN -> {
                    if (gameManager.getPlayers().size() < configManager.minPlayer) {
                        MegaWalls78.getInstance().getLogger().info("Game starts cancelled.");
                        gameManager.setState(GameState.WAITING);
                        ComponentUtil.sendMessage(Component.translatable("mw78.start.cancel", NamedTextColor.RED), Bukkit.getOnlinePlayers());
                        ComponentUtil.sendTitle(Component.empty(), Component.translatable("mw78.start.wait", NamedTextColor.RED), ComponentUtil.ONE_SEC_TIMES, Bukkit.getOnlinePlayers());
                    }
                }
            }
            if (flag && tick % 20 == 0) {
                if (!state.equals(GameState.WAITING) && !state.equals(GameState.ENDING)) {
                    if (timer <= 1000L) {
                        next(state);
                    }
                    else {
                        if (dmC) {
                            if (dmTimer <= 0L) {
                                dmC = false;
                                dm = true;
                                ComponentUtil.sendMessage(Component.translatable("mw78.dm.started", NamedTextColor.RED, TextDecoration.BOLD), Bukkit.getOnlinePlayers());
                            } else {
                                dmTimer -= 1000L;
                                if (dmTimer == 10000L || dmTimer <= 5000L && dmTimer > 0) {
                                    Component seconds = ComponentUtil.second(dmTimer, NamedTextColor.AQUA);
                                    ComponentUtil.sendMessage(Component.translatable("mw78.dm.countdown", NamedTextColor.RED, seconds), Bukkit.getOnlinePlayers());
                                }
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
                if (gameManager.inFighting() && !state.equals(GameState.OPENING) && !state.equals(GameState.COUNTDOWN)) {
                    for (GamePlayer gamePlayer : gameManager.getPlayers().values()) {
                        if (gamePlayer.getFinalDeaths() == 0) {
                            Player bukkitPlayer = gamePlayer.getBukkitPlayer();
                            if (bukkitPlayer != null) {
                                if (state.equals(GameState.PREPARING)) {
                                    gamePlayer.increaseEnergy(EnergyWay.PREPARATION);
                                } else {
                                    gamePlayer.increaseEnergy(isDm() ? EnergyWay.DM : EnergyWay.GAME);
                                }
                                if (isDm()) {
                                    Vector playerLocation = bukkitPlayer.getLocation().toVector();
                                    for (BoundingBox box : mid) {
                                        if (box.contains(playerLocation)) {
                                            bukkitPlayer.addPotionEffect(HUNGER);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (flag) {
                tick++;
            }
        }
        else if (!initializing) {
            init(gameManager);
        }
    }

    private void next(GameState state) {
        MegaWalls78 instance = MegaWalls78.getInstance();
        GameManager gameManager = instance.getGameManager();
        ConfigManager configManager = instance.getConfigManager();
        switch (state) {
            case COUNTDOWN -> {
                instance.getLogger().info("Game started.");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setHealth(0);
                }
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
                if (timer >= 10000L || timer >= 5000L) {
                    ComponentUtil.sendMessage(Component.translatable("mw78.gates", NamedTextColor.AQUA, Component.translatable("mw78.seconds", ComponentUtil.second(timer))), audience);
                }
                for (GameTeam team : gameManager.getTeams()) {
                    placeTeamGate(team);
                }
            }
            case OPENING -> {
                gameManager.setState(GameState.PREPARING);
                timer = configManager.preparingTime;
                for (GameTeam team : gameManager.getTeamPlayersMap().keySet()) {
                    if (gameManager.getTeamPlayersMap().get(team).isEmpty()) {
                        gameManager.getWither(team).setHealth(0);
                    }
                }
                destroyTeamGate();
                ComponentUtil.sendMessage(Component.translatable("mw78.prepare", NamedTextColor.RED).decorate(TextDecoration.BOLD), Bukkit.getOnlinePlayers());
                Bukkit.getScheduler().runTaskAsynchronously(MegaWalls78.getInstance(), () -> {
                    for (GameTeam team : gameManager.getTeams()) {
                        allowedBlocks.addAll(getTeamRegion(team));
                        allowedBlocks.removeAll(getPalace(team));
                        allowedBlocks.removeAll(getSpawn(team));
                    }
                });
            }
            case PREPARING -> {
                gameManager.setState(GameState.BUFFING);
                timer = configManager.buffingTime;
                destroyWalls();
                Bukkit.getScheduler().runTaskAsynchronously(MegaWalls78.getInstance(), () -> {
                    allowedBlocks.addAll(walls);
                    allowedBlocks.addAll(region);
                });
            }
            case BUFFING -> {
                gameManager.setState(GameState.FIGHTING);
                timer = configManager.fightingTime;
            }
            case FIGHTING -> {
                gameManager.setState(GameState.ENDING);
                allowedBlocks.clear();
            }
        }
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
                wither.customName(Component.translatable("mw78.wither.name", gameTeam.color(), gameTeam.name(), wither.name()));
                mcTeam.addEntity(wither);
                ((TeamWither) entity.getHandle()).setBossBar(gameManager.addWither(gameTeam, wither));
            });
        }
    }

    private void placeTeamGate(GameTeam team) {
        for (Location[] region : team.spawn()) {
            double minX = Math.min(region[0].getX(), region[1].getX());
            double maxX = Math.max(region[0].getX(), region[1].getX());
            double minY = Math.min(region[0].getY(), region[1].getY());
            double maxY = Math.max(region[0].getY(), region[1].getY());
            double minZ = Math.min(region[0].getZ(), region[1].getZ());
            double maxZ = Math.max(region[0].getZ(), region[1].getZ());
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

    public Set<Location> getAllowedBlocks() {
        return allowedBlocks;
    }

    public Set<Location> getTeamRegion(GameTeam team) {
        return regions.getOrDefault(team, EMPTY);
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
        ComponentUtil.sendMessage(Component.translatable("mw78.dm.start", NamedTextColor.RED, TextDecoration.BOLD), Bukkit.getOnlinePlayers());
        if (dmTimer == 10000L || dmTimer <= 5000L) {
            Component seconds = ComponentUtil.second(dmTimer, NamedTextColor.AQUA);
            ComponentUtil.sendMessage(Component.translatable("mw78.dm.countdown", NamedTextColor.RED, seconds), Bukkit.getOnlinePlayers());
        }
    }

    public boolean isDm() {
        return dm;
    }

    public boolean isDmC() {
        return dmC;
    }

    public Set<Location> getPalace(GameTeam team) {
        return palaces.getOrDefault(team, EMPTY);
    }

    public Set<Location> getSpawn(GameTeam team) {
        return spawns.getOrDefault(team, EMPTY);
    }
}