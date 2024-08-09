package icu.suc.megawalls78.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.record.GameMap;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Skin;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

public class ConfigManager {

    private final Configuration gameConfig;
    private final File mapFile;
    private final File skinFile;

    public String host;
    public int port;
    public String server;

    public String url;
    public String user;
    public String password;
    public String database;

    public int maxPlayer;
    public int minPlayer;
    public int maxSpec;
    public long waitingTime;
    public long openingTime;
    public long preparingTime;
    public long buffingTime;
    public long fightingTime;
    public long dmTime;
    public long respawnTime;
    public float witherHealth;

    public ConfigManager(Configuration gameConfig, String mapConfig, String skinConfig, String chatConfig) {
        this.gameConfig = gameConfig;
        this.mapFile = new File(MegaWalls78.getInstance().getDataFolder(), mapConfig);
        this.skinFile = new File(MegaWalls78.getInstance().getDataFolder(), skinConfig);
    }

    public void load() {
        save();

        File langDir = new File(MegaWalls78.getInstance().getDataFolder(), "lang");
        if (langDir.exists()) {
            TranslationRegistry registry = TranslationRegistry.create(new NamespacedKey(MegaWalls78.getInstance(), "lang"));
            for (File lang : FileUtils.listFiles(langDir, new String[]{"properties"}, false)) {
                Properties properties = new Properties();
                try (Reader reader = new FileReader(lang, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Map<String, MessageFormat> formats = Maps.newHashMap();
                properties.forEach((key, value) -> formats.put((String) key, new MessageFormat((String) value)));
                String[] s = lang.getName().split("\\.")[0].split("_");
                registry.registerAll(new Locale.Builder().setLanguage(s[0]).setRegion(s[1]).build(), formats);
            }
            GlobalTranslator.translator().addSource(registry);
        }

        ConfigurationSection redis = gameConfig.getConfigurationSection("redis");
        host = redis.getString("host");
        port = redis.getInt("port");

        ConfigurationSection mysql = gameConfig.getConfigurationSection("mysql");
        url = mysql.getString("url");
        user = mysql.getString("user");
        password = mysql.getString("password");
        database = mysql.getString("database");

        server = gameConfig.getString("server");
        maxPlayer = gameConfig.getInt("max-player", 100);
        minPlayer = gameConfig.getInt("min-player", 16);
        maxSpec = gameConfig.getInt("max-spec", 16);
        waitingTime = gameConfig.getLong("waiting-time", 30000L);
        openingTime = gameConfig.getLong("opening-time", 5000L);
        preparingTime = gameConfig.getLong("preparing-time", 360000L);
        buffingTime = gameConfig.getLong("buffing-time", 480000L);
        fightingTime = gameConfig.getLong("fighting-time", 1800000L);
        dmTime = gameConfig.getLong("dm-time", 10000L);
        respawnTime = gameConfig.getLong("respawn-time", 5000L);
        witherHealth = (float) gameConfig.getDouble("wither-health", 500.0D);

        MegaWalls78 instance = MegaWalls78.getInstance();

        Configuration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
        GameManager gameManager = instance.getGameManager();
        gameManager.setMap(new GameMap(mapConfig.getString("id"), mapConfig.getString("version"), mapConfig.getStringList("authors"), mapConfig.getInt("size"), toLocations(mapConfig.getList("spawn")), toLocations(mapConfig.getList("wall")), toLocations(mapConfig.getList("region")), toLocation(mapConfig.getList("spectator"))));
        gameManager.setTeams(Lists.newArrayList());
        ConfigurationSection teams = mapConfig.getConfigurationSection("team");
        for (String key : teams.getKeys(false)) {
            ConfigurationSection section = teams.getConfigurationSection(key);
            String color = section.getString("color");
            NamedTextColor namedTextColor;
            if (color.startsWith("#")) {
                namedTextColor = NamedTextColor.namedColor(Integer.parseInt(color.substring(1), 16));
            } else {
                namedTextColor = NamedTextColor.NAMES.value(color);
            }
            gameManager.getTeams().add(new GameTeam(key, namedTextColor, toLocations(section.getList("palace")), toLocations(section.getList("spawn")), toLocations(section.getList("region")), toLocation(section.getList("wither"))));
        }

        Configuration skinConfig = YamlConfiguration.loadConfiguration(skinFile);
        for (String key : skinConfig.getKeys(false)) {
            Identity identity = Identity.getIdentity(key);
            if (identity == null) {
                continue;
            }
            ConfigurationSection section = skinConfig.getConfigurationSection(key);
            if (section == null) {
                continue;
            }
            for (String s : section.getKeys(false)) {
                ConfigurationSection sc = Objects.requireNonNull(section.getConfigurationSection(s));
                instance.getSkinManager().addSkin(identity, new Skin(key + "." + s, sc.getString("value"), sc.getString("signature")));
            }
        }
    }

    public void save() {
        MegaWalls78 instance = MegaWalls78.getInstance();
        instance.saveDefaultConfig();
        if (!mapFile.exists()) {
            instance.saveResource(mapFile.getName(), false);
        }
        if (!skinFile.exists()) {
            instance.saveResource(skinFile.getName(), false);
        }
    }

    private Location[][] toLocations(List<?> locations) {
        Set<Location[]> set = Sets.newHashSet();
        World world = Bukkit.getWorlds().getFirst();
        for (Object location : locations) {
            List<Number> locA = ((List<List<Number>>) location).get(0);
            List<Number> locB = ((List<List<Number>>) location).get(1);
            List<Number> yp = ((List<List<Number>>) location).get(2);
            set.add(new Location[]{new Location(world, locA.get(0).doubleValue(), locA.get(1).doubleValue(), locA.get(2).doubleValue(), yp.get(0).floatValue(), yp.get(1).floatValue()), new Location(world, locB.get(0).doubleValue(), locB.get(1).doubleValue(), locB.get(2).doubleValue(), yp.get(0).floatValue(), yp.get(1).floatValue())});
        }

        return set.toArray(Location[][]::new);
    }

    private Location toLocation(List<?> location) {
        return new Location(Bukkit.getWorlds().getFirst(), ((Number) location.get(0)).doubleValue(), ((Number) location.get(1)).doubleValue(), ((Number) location.get(2)).doubleValue(), ((Number) location.get(3)).floatValue(), ((Number) location.get(4)).floatValue());
    }
}
