package icu.suc.megawalls78;

import icu.suc.megawalls78.command.*;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.listener.*;
import icu.suc.megawalls78.management.*;
import icu.suc.megawalls78.util.Redis;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import org.bukkit.Bukkit;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;

public final class MegaWalls78 extends JavaPlugin {

    private GameManager gameManager;
    private IdentityManager identityManager;
    private SkinManager skinManager;
    private ConfigManager configManager;
    private ScoreboardManager scoreboardManager;
    private DatabaseManager databaseManager;
    private EquipmentManager equipmentManager;
    private TriggerManager triggerManager;

    private static MegaWalls78 instance;
    private static ScoreboardLibrary scoreboardLib;

    private Jedis jedis;
    private JedisPubSub sub;

    @Override
    public void onEnable() {
        instance = this;
        initScoreboard();
        initManagers();
        registerCommands();
        registerListeners();
        loadConfig();
        initRedis();
        initSql();
        boostSmelting();
    }

    @Override
    public void onDisable() {
        closeScoreboard();
        saveConfig();
        closeRedis();
        closeSql();
    }

    private void initManagers() {
        configManager = new ConfigManager(getConfig(), "map.yml", "skin.yml", "chat.yml");
        identityManager = new IdentityManager();
        skinManager = new SkinManager();
        gameManager = new GameManager();
        scoreboardManager = new ScoreboardManager();
        equipmentManager = new EquipmentManager();
        triggerManager = new TriggerManager();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("cancel")).setExecutor(new CancelCommand());
        Objects.requireNonNull(getCommand("id")).setExecutor(new IdCommand());
        Objects.requireNonNull(getCommand("map")).setExecutor(new MapCommand());
        Objects.requireNonNull(getCommand("shout")).setExecutor(new ShoutCommand());
        Objects.requireNonNull(getCommand("energy")).setExecutor(new EnergyCommand());
        Objects.requireNonNull(getCommand("start")).setExecutor(new StartCommand());
        Objects.requireNonNull(getCommand("suicide")).setExecutor(new SuicideCommand());
        Objects.requireNonNull(getCommand("surface")).setExecutor(new SurfaceCommand());
        Objects.requireNonNull(getCommand("trigger")).setExecutor(new TriggerCommand());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipmentListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new IdentityListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new WitherListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(scoreboardManager, this);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public IdentityManager getIdentityManager() {
        return identityManager;
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static MegaWalls78 getInstance() {
        return instance;
    }

    public static ScoreboardLibrary getScoreboardLib() {
        return scoreboardLib;
    }

    public EquipmentManager getEquipmentManager() {
        return equipmentManager;
    }

    public TriggerManager getTriggerManager() {
        return triggerManager;
    }

    public void initScoreboard() {
        try {
            scoreboardLib = ScoreboardLibrary.loadScoreboardLibrary(this);
        } catch (NoPacketAdapterAvailableException e) {
            scoreboardLib = new NoopScoreboardLibrary();
        }
    }

    public void closeScoreboard() {
        scoreboardLib.close();
    }

    public void loadConfig() {
        configManager.load();
    }

    @Override
    public void saveConfig() {
        configManager.save();
    }

    public void initRedis() {
        jedis = Redis.get();
        sub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                String[] split = message.split("\\|");
                if (split[0].equals("games")) {
                    try (Jedis pub = Redis.get()) {
                        pub.publish("mw78", String.join("|", "game", configManager.server, gameManager.getMap().id(), String.valueOf(gameManager.inWaiting())));
                    }
                }
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> jedis.subscribe(sub, "mw78"));
    }

    public void initSql() {
        databaseManager = new DatabaseManager(configManager.url, configManager.user, configManager.password);
        try {
            databaseManager.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        databaseManager.init();
    }

    public void closeRedis() {
        sub.unsubscribe();
        Redis.close(jedis);
        if (!gameManager.getState().equals(GameState.ENDING)) {
            try (Jedis pub = Redis.get()) {
                pub.publish("mw78", String.join("|", "remove", MegaWalls78.getInstance().getConfigManager().server));
            }
        }
    }

    public void closeSql() {
        try {
            databaseManager.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void boostSmelting() {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        if (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (recipe instanceof CookingRecipe<?> cooking) {
                cooking.setCookingTime(cooking.getCookingTime() / 4);
            }
        }
    }
}
