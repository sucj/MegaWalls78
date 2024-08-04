package icu.suc.megawalls78.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.TeamWither;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.EnergyWay;
import icu.suc.megawalls78.management.ConfigManager;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GameRunner implements Runnable {

    private static final Set<Location> EMPTY = Set.of();

    public static boolean force;

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

    private final PotionEffect HUNGER = new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 9, true, false);

    public GameRunner() {
        this.barriers = Sets.newHashSet();
        this.allowedBlocks = Sets.newConcurrentHashSet();
        this.palaces = Maps.newHashMap();
        this.spawns = Maps.newHashMap();
        this.walls = Sets.newHashSet();
        this.region = Sets.newHashSet();
        this.regions = Maps.newHashMap();
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
            init = true;
            MegaWalls78.getInstance().getLogger().info("Map initialized.");

            try (Jedis pub = Redis.get()) {
                pub.publish("mw78", String.join("|", "game", MegaWalls78.getInstance().getConfigManager().server, gameManager.getMap().id(), String.valueOf(true)));
            }
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
                    if (gameManager.getPlayers().size() >= configManager.minPlayer || force) {
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
                    if (gameManager.getPlayers().size() < configManager.minPlayer && !force) {
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
                                MegaWalls78.getInstance().getScoreboardManager().updateSidebar(GameState.FIGHTING);
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    ComponentUtil.sendMessage(Component.translatable("mw78.dm.started", NamedTextColor.RED, TextDecoration.BOLD), player);
                                }
//                                for (Player player : Bukkit.getOnlinePlayers()) {
//                                    if (!gameManager.isSpectator(player)) {
//                                        player.addPotionEffect(GLOWING);
//                                    }
//                                }
                                Bukkit.getWorlds().getFirst().setHardcore(true);
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
                                    if (inMid(bukkitPlayer)) {
                                        if (EntityUtil.hasPotionEffect(bukkitPlayer, HUNGER)) {
                                            bukkitPlayer.removePotionEffect(PotionEffectType.HUNGER);
                                        }
                                    } else {
                                        if (!EntityUtil.hasPotionEffect(bukkitPlayer, HUNGER)) {
                                            bukkitPlayer.addPotionEffect(HUNGER);
                                        }
                                        if (timer % 10000 == 0) {
                                            ComponentUtil.sendMessage(Component.translatable("mw78.hunger", NamedTextColor.RED), bukkitPlayer);
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

    public void next(GameState state) {
        MegaWalls78 instance = MegaWalls78.getInstance();
        GameManager gameManager = instance.getGameManager();
        ConfigManager configManager = instance.getConfigManager();
        switch (state) {
            case COUNTDOWN -> {
                try (Jedis pub = Redis.get()) {
                    pub.publish("mw78", String.join("|", "game", MegaWalls78.getInstance().getConfigManager().server, gameManager.getMap().id(), String.valueOf(false)));
                }

                instance.getLogger().info("Game started.");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setHealth(0);
                }
                distributeTeams();
                gameManager.setState(GameState.OPENING);
                timer = configManager.openingTime;
                Component startMessage = Component.translatable("mw78.start", NamedTextColor.AQUA, TextDecoration.BOLD);
                Component title = Component.translatable("mw78.title", NamedTextColor.AQUA);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ComponentUtil.sendMessage(Component.empty(), player);
                    ComponentUtil.sendMessage(startMessage, player);

                    Component teamMessage = null;
                    GamePlayer gamePlayer = gameManager.getPlayer(player);
                    if (gamePlayer != null) {
                        GameTeam team = gamePlayer.getTeam();
                        if (team != null) {
                            teamMessage = Component.translatable("mw78.team", team.name().color(team.color()));
                        }
                    }
                    if (teamMessage != null) {
                        ComponentUtil.sendMessage(teamMessage, player);
                    }
                    ComponentUtil.sendMessage(Component.empty(), player);
                    ComponentUtil.sendTitle(title, teamMessage == null ? Component.empty() : teamMessage, ComponentUtil.DEFAULT_TIMES, player);

                    if (timer >= 10000L || timer >= 5000L) {
                        ComponentUtil.sendMessage(Component.translatable("mw78.gates", NamedTextColor.AQUA, Component.translatable("mw78.seconds", ComponentUtil.second(timer))), player);
                    }
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
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendPlayerListFooter(MegaWalls78.getInstance().getGameManager().footer().appendNewline().append(Component.text("MC.SUC.ICU", NamedTextColor.AQUA, TextDecoration.BOLD)));
                }
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

                MegaWalls78.getInstance().getLogger().info("Game end.");

                Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
                    List<Map.Entry<GameTeam, Pair<Integer, Integer>>> winners = Lists.newArrayList();
                    for (Map.Entry<GameTeam, Pair<Integer, Integer>> teamScore : gameManager.teamScore()) {
                        if (winners.isEmpty()) {
                            winners.add(teamScore);
                            continue;
                        }
                        Pair<Integer, Integer> value = teamScore.getValue();
                        Pair<Integer, Integer> last = winners.getLast().getValue();
                        if (last.getLeft() > value.getLeft()) {
                            break;
                        }
                        if (last.getRight() > value.getRight()) {
                            break;
                        }
                        winners.add(teamScore);
                    }

                    List<GameTeam> teams = Lists.newArrayList();
                    List<Component> components = Lists.newArrayList();
                    for (Map.Entry<GameTeam, Pair<Integer, Integer>> winner : winners) {
                        GameTeam team = winner.getKey();
                        teams.add(team);
                        components.add(team.name().color(team.color()));
                    }

                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    Component winnerMessage = Component.translatable("mw78.winner", Component.join(JoinConfiguration.builder().separator(Component.translatable("mw78.winner.join", NamedTextColor.WHITE)).build(), components)).decorate(TextDecoration.BOLD);
                    Component victory = Component.translatable("mw78.victory", NamedTextColor.GOLD);
                    Component defeat = Component.translatable("mw78.defeat", NamedTextColor.RED);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ComponentUtil.sendMessage(Component.empty(), player);
                        ComponentUtil.sendMessage(winnerMessage, player);

                        GamePlayer gamePlayer = gameManager.getPlayer(player);
                        if (gamePlayer == null) {
                            continue;
                        }
                        if (teams.contains(gamePlayer.getTeam())) {
                            ComponentUtil.sendTitle(victory, Component.empty(), ComponentUtil.DEFAULT_TIMES, player);
                            Bukkit.getScheduler().runTask(instance, () -> player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET));
                        } else {
                            ComponentUtil.sendTitle(defeat, Component.empty(), ComponentUtil.DEFAULT_TIMES, player);
                        }
                    }

                    GamePlayer mvp = null;
                    double mvpScore = 0;
                    GamePlayer mincer = null;
                    double mincerScore = 0;
                    GamePlayer rusher = null;
                    double rusherScore = 0;
                    GamePlayer resister = null;
                    double resisterScore = 0;
                    for (GamePlayer gamePlayer : gameManager.getPlayers().values()) {
                        int fk = gamePlayer.getFinalKills();
                        int a = gamePlayer.getFinalDeaths() == 0 ? fk * 100 : fk * 50;
                        double damageDealt = gamePlayer.getDamageDealt();
                        double damageWither = gamePlayer.getDamageWither();
                        double damagePlayer = damageDealt - damageWither;
                        double damageGuard = gamePlayer.getDamageGuard();
                        double damageTaken = gamePlayer.getDamageTaken();
                        double b = damagePlayer + damageWither * 1.05 + damageGuard * 1.01 + damageTaken * 0.5;
                        double score = a + b;
                        if (teams.contains(gamePlayer.getTeam())) {
                            score += 100;
                        }
                        if (score > mvpScore) {
                            mvpScore = score;
                            mvp = gamePlayer;
                        } else if (score != 0 && score == mvpScore) {
                            if (teams.contains(gamePlayer.getTeam())) {
                                mvpScore = score;
                                mvp = gamePlayer;
                            }
                        }
                        if (damageDealt > mincerScore) {
                            mincerScore = damageDealt;
                            mincer = gamePlayer;
                        } else if (damageDealt != 0 && damageDealt == mincerScore) {
                            if (teams.contains(gamePlayer.getTeam())) {
                                mincerScore = damageDealt;
                                mincer = gamePlayer;
                            }
                        }
                        if (damageWither > rusherScore) {
                            rusherScore = damageWither;
                            rusher = gamePlayer;
                        } else if (damageWither != 0 && damageWither == rusherScore) {
                            if (teams.contains(gamePlayer.getTeam())) {
                                rusherScore = damageWither;
                                rusher = gamePlayer;
                            }
                        }
                        if (damageTaken > resisterScore) {
                            resisterScore = damageTaken;
                            resister = gamePlayer;
                        } else if (damageTaken != 0 && damageTaken == resisterScore) {
                            if (teams.contains(gamePlayer.getTeam())) {
                                resisterScore = damageTaken;
                                resister = gamePlayer;
                            }
                        }
                    }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (mvp != null || mincer != null || rusher != null || resister != null) {
                        ComponentUtil.sendMessage(Component.empty(), Bukkit.getOnlinePlayers());
                    }

                    if (mincer != null) {
                        UUID uuid = mincer.getUuid();
                        GameTeam team = mincer.getTeam();
                        Component mincerMessage = Component.translatable("mw78.mincer", Component.translatable("ms78.brackets", team.color(), team.chat()).append(Component.space()).append(LP.getPrefix(uuid).append(Component.text(Bukkit.getOfflinePlayer(uuid).getName(), LP.getNameColor(uuid))))).decorate(TextDecoration.BOLD);
                        Component mincerSummary = Component.translatable("mw78.mincer.summary", NamedTextColor.GRAY, Component.text(Formatters.NUMBER.format(mincerScore)));
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            ComponentUtil.sendMessage(mincerMessage, player);
                            ComponentUtil.sendMessage(mincerSummary, player);

                            if (mincer.getUuid().equals(player.getUniqueId())) {
                                Bukkit.getScheduler().runTask(instance, () -> player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET));
                            }
                        }
                    }

                    if (rusher != null) {
                        UUID uuid = rusher.getUuid();
                        GameTeam team = rusher.getTeam();
                        Component rusherMessage = Component.translatable("mw78.rusher", Component.translatable("ms78.brackets", team.color(), team.chat()).append(Component.space()).append(LP.getPrefix(uuid).append(Component.text(Bukkit.getOfflinePlayer(uuid).getName(), LP.getNameColor(uuid))))).decorate(TextDecoration.BOLD);
                        Component rusherSummary = Component.translatable("mw78.rusher.summary", NamedTextColor.GRAY, Component.text(Formatters.NUMBER.format(rusherScore)));
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            ComponentUtil.sendMessage(rusherMessage, player);
                            ComponentUtil.sendMessage(rusherSummary, player);

                            if (rusher.getUuid().equals(player.getUniqueId())) {
                                Bukkit.getScheduler().runTask(instance, () -> player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET));
                            }
                        }
                    }

                    if (resister != null) {
                        UUID uuid = resister.getUuid();
                        GameTeam team = resister.getTeam();
                        Component resisterMessage = Component.translatable("mw78.resister", Component.translatable("ms78.brackets", team.color(), team.chat()).append(Component.space()).append(LP.getPrefix(uuid).append(Component.text(Bukkit.getOfflinePlayer(uuid).getName(), LP.getNameColor(uuid))))).decorate(TextDecoration.BOLD);
                        Component resisterSummary = Component.translatable("mw78.resister.summary", NamedTextColor.GRAY, Component.text(Formatters.NUMBER.format(resisterScore)));
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            ComponentUtil.sendMessage(resisterMessage, player);
                            ComponentUtil.sendMessage(resisterSummary, player);

                            if (resister.getUuid().equals(player.getUniqueId())) {
                                Bukkit.getScheduler().runTask(instance, () -> player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET));
                            }
                        }
                    }

                    if (mvp != null) {
                        UUID mvpUuid = mvp.getUuid();
                        GameTeam team = mvp.getTeam();
                        Component mvpMessage = Component.translatable("mw78.mvp", Component.translatable("ms78.brackets", team.color(), team.chat()).append(Component.space()).append(LP.getPrefix(mvpUuid).append(Component.text(Bukkit.getOfflinePlayer(mvpUuid).getName(), LP.getNameColor(mvpUuid))))).decorate(TextDecoration.BOLD);
                        Component mvpSummary = Component.translatable("mw78.mvp.summary", NamedTextColor.GRAY, Component.text(Formatters.COMPASS.format(mvpScore)));
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            ComponentUtil.sendMessage(mvpMessage, player);
                            ComponentUtil.sendMessage(mvpSummary, player);

                            if (mvp.getUuid().equals(player.getUniqueId())) {
                                Bukkit.getScheduler().runTask(instance, () -> player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET));
                            }
                        }
                    }

                    if (mvp != null || mincer != null || rusher != null || resister != null) {
                        ComponentUtil.sendMessage(Component.empty(), Bukkit.getOnlinePlayers());
                    }
                });
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
        MegaWalls78.getInstance().getScoreboardManager().updateSidebar(GameState.FIGHTING);
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

    public boolean inMid(Player player) {
        Location location = player.getLocation().toBlockLocation();
        location.setYaw(0);
        location.setPitch(0);
        return region.contains(location);
    }
}