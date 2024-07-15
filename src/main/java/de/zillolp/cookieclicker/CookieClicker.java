package de.zillolp.cookieclicker;

import de.zillolp.cookieclicker.bstats.Metrics;
import de.zillolp.cookieclicker.commands.maincommands.CookieClickerCommand;
import de.zillolp.cookieclicker.commands.subcommands.*;
import de.zillolp.cookieclicker.config.ConfigManager;
import de.zillolp.cookieclicker.config.customconfigs.*;
import de.zillolp.cookieclicker.database.DatabaseConnector;
import de.zillolp.cookieclicker.hologram.HologramManager;
import de.zillolp.cookieclicker.inventory.InventoryManager;
import de.zillolp.cookieclicker.listener.*;
import de.zillolp.cookieclicker.manager.*;
import de.zillolp.cookieclicker.placeholder.PlaceholderListener;
import de.zillolp.cookieclicker.protections.ProtectionManager;
import de.zillolp.cookieclicker.statswall.StatsWallManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class CookieClicker extends JavaPlugin {
    private CookieClicker plugin;
    private PluginConfig pluginConfig;
    private LanguageConfig languageConfig;
    private LocationConfig locationConfig;
    private MySQLConfig mysqlConfig;
    private PermissionsConfig permissionsConfig;
    private DatabaseConnector databaseConnector;
    private ConfigManager configManager;
    private CookieClickerManager cookieClickerManager;
    private DatabaseManager databaseManager;
    private SetupManager setupManager;
    private HologramManager hologramManager;
    private StatsWallManager statsWallManager;
    private PriceManager priceManager;
    private InventoryManager inventoryManager;
    private ProtectionManager protectionManager;
    private SoundManager soundManager;

    @Override
    public void onEnable() {
        this.plugin = this;
        this.registerConfigs();
        this.connectDatabase();
        if ((!(databaseConnector.checkConnection())) || checkVersion()) {
            if(checkVersion()) {
                getLogger().warning("You are using the CookieClicker version for 1.21 on a non 1.21 server, which is why the plugin does not start!");
                getLogger().warning("Change your version to a version programmed for your server version.");
            }
            this.getCommand("cookieclicker").setExecutor(new CookieClickerCommand(plugin));
            return;
        }
        this.registerManager();
        this.registerCommands();
        this.registerListener(Bukkit.getPluginManager());
        this.loadPlayers();
        this.registerMetrics();
    }

    @Override
    public void onDisable() {
        if ((!(databaseConnector.checkConnection())) || checkVersion()) {
            return;
        }
        this.unloadPlayers();
        this.databaseConnector.close();
    }

    private void connectDatabase() {
        boolean useMySQL = pluginConfig.getFileConfiguration().getBoolean("MySQL");
        FileConfiguration mysqlConfiguration = mysqlConfig.getFileConfiguration();
        String address = mysqlConfiguration.getString("Host");
        String port = mysqlConfiguration.getString("Port");
        String databaseName = mysqlConfiguration.getString("Database");
        String username = mysqlConfiguration.getString("User");
        String password = mysqlConfiguration.getString("Password");
        if (address == null || port == null || databaseName == null | username == null || password == null) {
            address = "N/A";
            port = "N/A";
            databaseName = "N/A";
            username = "N/A";
            password = "N/A";
        }
        databaseConnector = new DatabaseConnector(plugin, useMySQL, "cookieclicker", address, port, databaseName, username, password);
        databaseConnector.open();
    }

    private void registerConfigs() {
        this.configManager = new ConfigManager(plugin);
        this.pluginConfig = configManager.getPluginConfig();
        this.languageConfig = configManager.getLanguageConfig();
        this.locationConfig = configManager.getLocationConfig();
        this.mysqlConfig = configManager.getMySQLConfig();
        this.permissionsConfig = configManager.getPermissionsConfig();
    }

    private void registerManager() {
        this.databaseManager = new DatabaseManager(plugin);
        this.hologramManager = new HologramManager(plugin);
        this.inventoryManager = new InventoryManager(plugin);
        this.protectionManager = new ProtectionManager(plugin);
        this.cookieClickerManager = new CookieClickerManager(plugin);
        this.setupManager = new SetupManager(plugin);
        this.statsWallManager = new StatsWallManager(plugin);
        this.priceManager = new PriceManager();
        this.soundManager = new SoundManager(plugin);
    }

    private void registerCommands() {
        CookieClickerCommand cookieClickerCommand = new CookieClickerCommand(plugin);
        this.getCommand("cookieclicker").setExecutor(cookieClickerCommand);
        cookieClickerCommand.registerSubCommand(new HelpSubCommand(plugin, "help"));
        cookieClickerCommand.registerSubCommand(new StatsSubCommand(plugin, "stats"));
        cookieClickerCommand.registerSubCommand(new ReloadSubCommand(plugin, "reload"));
        cookieClickerCommand.registerSubCommand(new ListSubCommand(plugin, "list"));
        cookieClickerCommand.registerSubCommand(new SetSubCommand(plugin, "set", "clicker"));
        cookieClickerCommand.registerSubCommand(new SetSubCommand(plugin, "set", "resettimer"));
        cookieClickerCommand.registerSubCommand(new SetSubCommand(plugin, "set", "statswall", "alltime;time"));
        cookieClickerCommand.registerSubCommand(new RemoveSubCommand(plugin, "remove", "clicker"));
        cookieClickerCommand.registerSubCommand(new ResetSubCommand(plugin, "reset"));
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new AntiAFKListener(plugin), plugin);
        pluginManager.registerEvents(new ClickerClickListener(plugin), plugin);
        pluginManager.registerEvents(new PlayerConnectionListener(plugin), plugin);
        pluginManager.registerEvents(new SetupListener(plugin), plugin);
        pluginManager.registerEvents(new StatsWallListener(plugin), plugin);
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            new PlaceholderListener(plugin).register();
        }
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(plugin, 11733);
        metrics.addCustomChart(new Metrics.AdvancedPie("database_type", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            if (pluginConfig.getFileConfiguration().getBoolean("MySQL")) {
                valueMap.put("MySQL", 1);
            } else {
                valueMap.put("SQLite", 1);
            }
            return valueMap;
        }));
        metrics.addCustomChart(new Metrics.SingleLineChart("registered_players", () -> databaseManager.getRegisteredPlayerAmount()));
        metrics.addCustomChart(new Metrics.AdvancedPie("minecraft_version_players", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            String[] versionNumber = Bukkit.getBukkitVersion().replace(".", "-").split("-");
            String version = "v" + versionNumber[0] + "." + versionNumber[1] + "." + versionNumber[2];
            valueMap.put(version, Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
    }

    public void loadPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            cookieClickerManager.registerPlayer(player, false);
        }
        databaseManager.loadProfiles();
    }

    public void unloadPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            cookieClickerManager.unregisterPlayer(player, false);
        }
    }

    public boolean checkVersion() {
        String bukkitVersion = Bukkit.getBukkitVersion().replace(".", "-");
        return Integer.parseInt(bukkitVersion.split("-")[1]) < 21;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }


    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public LocationConfig getLocationConfig() {
        return locationConfig;
    }

    public PermissionsConfig getPermissionsConfig() {
        return permissionsConfig;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SetupManager getSetupManager() {
        return setupManager;
    }

    public CookieClickerManager getCookieClickerManager() {
        return cookieClickerManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public StatsWallManager getStatsWallManager() {
        return statsWallManager;
    }

    public void setStatsWallManager(StatsWallManager statsWallManager) {
        this.statsWallManager = statsWallManager;
    }

    public PriceManager getPriceManager() {
        return priceManager;
    }

    public ProtectionManager getProtectionManager() {
        return protectionManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}
