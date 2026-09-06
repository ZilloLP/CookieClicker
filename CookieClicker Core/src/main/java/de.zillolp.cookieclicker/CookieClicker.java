package de.zillolp.cookieclicker;

import de.zillolp.cookieclicker.bstats.Metrics;
import de.zillolp.cookieclicker.commands.maincommands.CookieClickerCommand;
import de.zillolp.cookieclicker.commands.subcommands.*;
import de.zillolp.cookieclicker.config.ConfigManager;
import de.zillolp.cookieclicker.config.customconfigs.*;
import de.zillolp.cookieclicker.custominventories.CustomInventory;
import de.zillolp.cookieclicker.database.DatabaseConnector;
import de.zillolp.cookieclicker.database.DatabaseManager;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import de.zillolp.cookieclicker.handler.ClickerHandler;
import de.zillolp.cookieclicker.interfaces.ItemBuilder;
import de.zillolp.cookieclicker.interfaces.NmsBridge;
import de.zillolp.cookieclicker.interfaces.PacketReader;
import de.zillolp.cookieclicker.listener.*;
import de.zillolp.cookieclicker.listener.inventories.*;
import de.zillolp.cookieclicker.manager.*;
import de.zillolp.cookieclicker.placeholder.PlaceholderListener;
import de.zillolp.cookieclicker.profiles.ClickerInventoryProfile;
import de.zillolp.cookieclicker.runnables.*;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CookieClicker extends JavaPlugin {
    private final ArrayList<CustomUpdater> customUpdaters = new ArrayList<>();
    private PluginConfig pluginConfig;
    private LanguageConfig languageConfig;
    private LocationConfig locationConfig;
    private MySQLConfig mySQLConfig;
    private PermissionsConfig permissionsConfig;
    private VersionManager versionManager;
    private DatabaseConnector databaseConnector;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private ClickerPlayerManager clickerPlayerManager;
    private CookieClickerManager cookieClickerManager;
    private HologramManager hologramManager;
    private DesignManager designManager;
    private SoundManager soundManager;
    private ClickerEventManager clickerEventManager;
    private ReflectionUtil reflectionUtil;
    private NmsBridge nmsBridge;
    private ItemBuilder itemBuilder;
    private ClickerHandler clickerHandler;
    private PacketReader packetReader;
    private StatsWallUpdater statsWallUpdater;
    private ResetTimerUpdater resetTimerUpdater;

    @Override
    public void onEnable() {
        registerConfigs();
        if (!(checkCompatibility())) {
            return;
        }
        registerManager();
        reflectionUtil = new ReflectionUtil(this);
        nmsBridge = versionManager.getNmsBridge();
        clickerHandler = new ClickerHandler(this);
        itemBuilder = versionManager.getItemBuilder();
        packetReader = versionManager.getPacketReader();
        registerCommands();
        registerListener(Bukkit.getPluginManager());
        loadPlayers();
        registerUpdaters();
        registerMetrics();
    }

    @Override
    public void onDisable() {
        if ((!(versionManager.checkVersion())) || (!(databaseConnector.hasConnection()))) {
            return;
        }
        stopUpdaters();
        closeInventories();
        unloadPlayers();
        databaseConnector.close();
    }

    private void registerConfigs() {
        configManager = new ConfigManager(this);
        pluginConfig = configManager.getPluginConfig();
        languageConfig = configManager.getLanguageConfig();
        locationConfig = configManager.getLocationConfig();
        mySQLConfig = configManager.getMySQLConfig();
        permissionsConfig = configManager.getPermissionsConfig();
    }

    private void connectDatabase() {
        FileConfiguration mysqlConfiguration = mySQLConfig.getFileConfiguration();
        String serverName = mysqlConfiguration.getString("Host", "localhost");
        String port = mysqlConfiguration.getString("Port", "25565");
        String databaseName = mysqlConfiguration.getString("Database", "cookieclicker");
        String user = mysqlConfiguration.getString("User", "root");
        String password = mysqlConfiguration.getString("Password", "123+");
        databaseConnector = new DatabaseConnector(this, pluginConfig.getFileConfiguration().getBoolean("MySQL", false), "cookieclicker", serverName, port, databaseName, user, password);
    }

    private boolean checkCompatibility() {
        Logger logger = getLogger();
        versionManager = new VersionManager(this);
        boolean isConnected = false;
        boolean isVersion = versionManager.checkVersion();
        if (!(isVersion)) {
            for (String wrongVersionMessage : versionManager.getWrongVersionMessage()) {
                logger.warning(wrongVersionMessage);
            }
        } else {
            connectDatabase();
            isConnected = databaseConnector.hasConnection();
            if (!(isConnected)) {
                logger.log(Level.SEVERE, "Could not connect to Database!");
                databaseConnector.close();
            }
        }
        boolean isCompatible = isVersion && isConnected;
        if (!(isCompatible)) {
            CookieClickerCommand cookieClickerCommand = new CookieClickerCommand(this);
            PluginCommand cookieClickerPluginCommand = getCommand("cookieclicker");
            if (cookieClickerPluginCommand != null) {
                cookieClickerPluginCommand.setExecutor(cookieClickerCommand);
            }
        }
        return isCompatible;
    }

    private void registerManager() {
        databaseManager = new DatabaseManager(this);
        cookieClickerManager = new CookieClickerManager(this);
        hologramManager = new HologramManager(this);
        clickerPlayerManager = new ClickerPlayerManager(this);
        designManager = new DesignManager(this);
        soundManager = new SoundManager(this);
        clickerEventManager = new ClickerEventManager();
    }

    private void registerCommands() {
        CookieClickerCommand cookieClickerCommand = new CookieClickerCommand(this);
        PluginCommand cookieClickerPluginCommand = getCommand("cookieclicker");
        if (cookieClickerPluginCommand != null) {
            cookieClickerPluginCommand.setExecutor(cookieClickerCommand);
            cookieClickerCommand.registerSubCommand(new HelpSubCommand(this, "help"));
            cookieClickerCommand.registerSubCommand(new ListSubCommand(this, "list"));
            cookieClickerCommand.registerSubCommand(new ModifySubCommand(this, "modify", "", "add;set;remove", "COOKIES;PER_CLICK;CLICKER_CLICKS"));
            cookieClickerCommand.registerSubCommand(new ReloadSubCommand(this, "reload"));
            cookieClickerCommand.registerSubCommand(new RemoveSubCommand(this, "remove", "clicker;resettimer"));
            cookieClickerCommand.registerSubCommand(new ResetSubCommand(this, "reset"));
            cookieClickerCommand.registerSubCommand(new SetSubCommand(this, "set", "clicker;resettimer"));
            cookieClickerCommand.registerSubCommand(new SetSubCommand(this, "set", "statswall", "alltime;time"));
            cookieClickerCommand.registerSubCommand(new StatsSubCommand(this, "stats"));
        }
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new ClickerListener(this), this);
        pluginManager.registerEvents(new PlayerConnectionListener(this), this);
        pluginManager.registerEvents(new PlayerMovementListener(this), this);
        pluginManager.registerEvents(new SetupListener(this), this);
        pluginManager.registerEvents(new StatsWallListener(this), this);
        pluginManager.registerEvents(new CookieExplosionListener(this), this);
        pluginManager.registerEvents(new HomeInventoryListener(this, CustomInventoryType.HOME), this);
        pluginManager.registerEvents(new DesignInventoryListener(this, CustomInventoryType.DESIGN), this);
        pluginManager.registerEvents(new HitParticleDesignInventoryListener(this, CustomInventoryType.HIT_PARTICLE_DESIGN), this);
        pluginManager.registerEvents(new MenuDesignInventoryListener(this, CustomInventoryType.MENU_DESIGN), this);
        pluginManager.registerEvents(new BlockDesignInventoryListener(this, CustomInventoryType.BLOCK_DESIGN), this);
        pluginManager.registerEvents(new PremiumShopInventoryListener(this, CustomInventoryType.PREMIUM_SHOP), this);
        pluginManager.registerEvents(new ShopInventoryListener(this, CustomInventoryType.SHOP), this);
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            new PlaceholderListener(this).register();
        }
    }

    public void loadPlayers() {
        databaseManager.loadProfiles();
        for (Player player : Bukkit.getOnlinePlayers()) {
            clickerPlayerManager.registerPlayer(player);
            packetReader.inject(player);
            designManager.sendClickerBlockDesign(player);
        }
    }

    private void registerUpdaters() {
        statsWallUpdater = new StatsWallUpdater(this);
        resetTimerUpdater = new ResetTimerUpdater(this);
        customUpdaters.add(new HologramUpdater(this));
        customUpdaters.add(new PlayerProtectionUpdater(this));
        customUpdaters.add(new StatsUpdater(this, pluginConfig.getCacheSynchronizationTime()));
        customUpdaters.add(new EventActionbarUpdater(this));
        customUpdaters.add(new EventUpdater(this));
        customUpdaters.add(statsWallUpdater);
        customUpdaters.add(resetTimerUpdater);
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, 11733);
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


    private void stopUpdaters() {
        for (CustomUpdater customUpdater : customUpdaters) {
            customUpdater.stop();
        }
    }

    public void unloadPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            clickerEventManager.deactivateEvents(player.getUniqueId());
            clickerPlayerManager.unregisterPlayer(player);
            packetReader.unInject(player);
        }
    }

    public void closeInventories() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ClickerInventoryProfile clickerInventoryProfile = clickerPlayerManager.getInventoryProfile(player);
            for (CustomInventory customInventory : clickerInventoryProfile.getCustomInventories().values()) {
                Inventory inventory = reflectionUtil.getOpenInventory(player);
                if (inventory != null && inventory == customInventory.getInventory()) {
                    player.closeInventory();
                    break;
                }
            }
        }
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

    public VersionManager getVersionManager() {
        return versionManager;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ClickerPlayerManager getClickerPlayerManager() {
        return clickerPlayerManager;
    }

    public CookieClickerManager getCookieClickerManager() {
        return cookieClickerManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public DesignManager getDesignManager() {
        return designManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public ClickerEventManager getClickerEventManager() {
        return clickerEventManager;
    }

    public ReflectionUtil getReflectionUtil() {
        return reflectionUtil;
    }

    public NmsBridge getNmsBridge() {
        return nmsBridge;
    }

    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }

    public ClickerHandler getClickerHandler() {
        return clickerHandler;
    }

    public PacketReader getPacketReader() {
        return packetReader;
    }

    public StatsWallUpdater getStatsWallUpdater() {
        return statsWallUpdater;
    }

    public ResetTimerUpdater getResetTimerUpdater() {
        return resetTimerUpdater;
    }
}
