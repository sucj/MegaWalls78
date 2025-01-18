package icu.suc.megawalls78;

import com.mojang.brigadier.tree.LiteralCommandNode;
import icu.suc.megawalls78.command.*;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.listener.*;
import icu.suc.megawalls78.management.*;
import icu.suc.megawalls78.util.Redis;
import icu.suc.megawalls78.util.Scheduler;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

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
    }

    @Override
    public void onDisable() {
        closeScoreboard();
        saveConfig();
        closeRedis();
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
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();

            // State
            if (getCommand("start") instanceof PluginCommand start) {
                commands.register(StartCommand.register(start.getName(), start.getPermission()), start.getDescription(), start.getAliases());
            }
            if (getCommand("cancel") instanceof PluginCommand cancel) {
                commands.register(CancelCommand.register(cancel.getName(), cancel.getPermission()), cancel.getDescription(), cancel.getAliases());
            }

            // Debug
            if (getCommand("energy") instanceof PluginCommand energy) {
                commands.register(EnergyCommand.register(energy.getName(), energy.getPermission()), energy.getDescription(), energy.getAliases());
            }

            // Fighting
            if (getCommand("shout") instanceof PluginCommand shout) {
                commands.register((LiteralCommandNode) ShoutCommand.register(shout.getName(), shout.getPermission()), shout.getDescription(), shout.getAliases());
            }
            if (getCommand("suicide") instanceof PluginCommand suicide) {
                commands.register(SuicideCommand.register(suicide.getName(), suicide.getPermission()), suicide.getDescription(), suicide.getAliases());
            }
            if (getCommand("surface") instanceof PluginCommand surface) {
                commands.register(SurfaceCommand.register(surface.getName(), surface.getPermission()), surface.getDescription(), surface.getAliases());
            }
            if (getCommand("teamchest") instanceof PluginCommand teamchest) {
                commands.register(TeamchestCommand.register(teamchest.getName(), teamchest.getPermission()), teamchest.getDescription(), teamchest.getAliases());
            }

            // Misc
            if (getCommand("trait") instanceof PluginCommand trait) {
                commands.register(TraitCommand.register(trait.getName(), trait.getPermission()), trait.getDescription(), trait.getAliases());
            }
            if (getCommand("trigger") instanceof PluginCommand trigger) {
                commands.register(TriggerCommand.register(trigger.getName(), trigger.getPermission()), trigger.getDescription(), trigger.getAliases());
            }
        });

//        Objects.requireNonNull(getCommand("id")).setExecutor(new IdCommand());
//        Objects.requireNonNull(getCommand("map")).setExecutor(new MapCommand());
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
        Scheduler.runTaskAsync(() -> jedis.subscribe(sub, "mw78"));
    }

    public void initSql() {
        databaseManager = new DatabaseManager(configManager.url, configManager.user, configManager.password);
        databaseManager.init();
    }

    public void closeRedis() {
        try {
            sub.unsubscribe();
            if (!gameManager.getState().equals(GameState.ENDING)) {
                try (Jedis jedis = Redis.get()) {
                    jedis.publish("mw78", String.join("|", "remove", MegaWalls78.getInstance().getConfigManager().server));
                }
            }
            Redis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
