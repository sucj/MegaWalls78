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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Objects;

public final class MegaWalls78 extends JavaPlugin {

    private GameManager gameManager;
    private IdentityManager identityManager;
    private SkinManager skinManager;
    private ConfigManager configManager;
    private ScoreboardManager scoreboardManager;

    private static MegaWalls78 instance;
    private static ScoreboardLibrary scoreboardLib;

    private Jedis jedis;
    private JedisPubSub sub;

    @Override
    public void onEnable() {
        instance = this;
        try {
            scoreboardLib = ScoreboardLibrary.loadScoreboardLibrary(this);
        } catch (NoPacketAdapterAvailableException e) {
            scoreboardLib = new NoopScoreboardLibrary();
        }
        initManagers();
        registerCommands();
        registerListeners();
        configManager.load();

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

    @Override
    public void onDisable() {
        scoreboardLib.close();
        configManager.save();

        sub.unsubscribe();
        Redis.close(jedis);
        if (!gameManager.getState().equals(GameState.ENDING)) {
            try (Jedis pub = Redis.get()) {
                pub.publish("mw78", String.join("|", "remove", MegaWalls78.getInstance().getConfigManager().server));
            }
        }
    }

    private void initManagers() {
        configManager = new ConfigManager(getConfig(), "map.yml", "skin.yml", "chat.yml");
        identityManager = new IdentityManager();
        skinManager = new SkinManager();
        gameManager = new GameManager();
        scoreboardManager = new ScoreboardManager();
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
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new SkillListener(), this);
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

    public static MegaWalls78 getInstance() {
        return instance;
    }

    public static ScoreboardLibrary getScoreboardLib() {
        return scoreboardLib;
    }
}
