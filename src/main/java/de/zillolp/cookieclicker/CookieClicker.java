package de.zillolp.cookieclicker;

import de.zillolp.cookieclicker.bstats.Metrics;
import de.zillolp.cookieclicker.commands.CookieClickerCommand;
import de.zillolp.cookieclicker.config.*;
import de.zillolp.cookieclicker.database.DatabaseConnector;
import de.zillolp.cookieclicker.database.DatabaseManager;
import de.zillolp.cookieclicker.listener.*;
import de.zillolp.cookieclicker.placeholder.Expansion;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.runnables.AlltimeUpdater;
import de.zillolp.cookieclicker.runnables.AntiAutoClicker;
import de.zillolp.cookieclicker.runnables.ResetTimerUpdater;
import de.zillolp.cookieclicker.runnables.TimeUpdater;
import de.zillolp.cookieclicker.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class CookieClicker extends JavaPlugin {
    public static CookieClicker cookieClicker;

    private TimeUpdater timeUpdater;
    private AlltimeUpdater alltimeUpdater;
    private DatabaseConnector databaseConnector;
    private DatabaseManager databaseManager;

    private ResetTimerUpdater resetTimerUpdater;
    private HashMap<String, Location> clickerLocations;
    private HashMap<Location, String> statsWallLocations;
    private HashMap<UUID, PlayerProfile> playerProfiles;

    @Override
    public void onEnable() {
        cookieClicker = this;
        if (register()) {
            init(Bukkit.getPluginManager());
        }
    }

    @Override
    public void onDisable() {
        if (databaseConnector.checkConnection()) {
            databaseConnector.disabled = true;
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerProfiles.get(player.getUniqueId()).uploadProfile();
            }
            try {
                databaseConnector.getConnection().close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private boolean register() {
        if (!(new ConfigUtil("config.yml").exist())) {
            saveResource("config.yml", false);
        }
        if (!(new ConfigUtil("language.yml").exist())) {
            saveResource("language.yml", false);
        }
        if (!(new ConfigUtil("locations.yml").exist())) {
            saveResource("locations.yml", false);
        }
        if (!(new ConfigUtil("mysql.yml").exist())) {
            saveResource("mysql.yml", false);
        }
        if (!(new ConfigUtil("permissions.yml").exist())) {
            saveResource("permissions.yml", false);
        }
        ConfigTools.load();
        LanguageTools.load();
        MySQLTools.load();
        PermissionTools.load();

        databaseConnector = new DatabaseConnector(ConfigTools.isMysql(), "cookieclicker", MySQLTools.getHost(), MySQLTools.getPort(), MySQLTools.getDatabase(), MySQLTools.getUser(), MySQLTools.getPassword());
        databaseConnector.open();

        if (databaseConnector.getConnection() == null) {
            Bukkit.getPluginManager().disablePlugin(cookieClicker);
            return false;
        }
        databaseManager = new DatabaseManager(databaseConnector);
        return databaseConnector.checkConnection();
    }

    private void init(PluginManager pluginManager) {
        new UpdateChecker().checkVersion();
        loadMetrics();
        ReflectionUtil.initialize();
        ActionBarUtil.initialize();
        HologramUtil.initialize();
        statsWallLocations = new HashMap<>();
        playerProfiles = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerProfiles.put(player.getUniqueId(), new PlayerProfile(player));
        }
        getCommand("cookieclicker").setExecutor(new CookieClickerCommand());
        pluginManager.registerEvents(new AntiAFKListener(), this);
        pluginManager.registerEvents(new ClickerListener(), this);
        pluginManager.registerEvents(new DesignInventoryListener(), this);
        pluginManager.registerEvents(new HomeInventoryListener(), this);
        pluginManager.registerEvents(new PlayerConnectionListener(), this);
        pluginManager.registerEvents(new PremiumShopInventoryListener(), this);
        pluginManager.registerEvents(new SetupListener(), this);
        pluginManager.registerEvents(new ShopBuyListener(), this);
        pluginManager.registerEvents(new ShopInventoryListener(), this);
        pluginManager.registerEvents(new StatsWallListener(), this);
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            new Expansion().register();
        }
        loadCookieClicker();
        new AntiAutoClicker();
        alltimeUpdater = new AlltimeUpdater();
        resetTimerUpdater = new ResetTimerUpdater();
        timeUpdater = new TimeUpdater();
    }


    private void loadMetrics() {
        Metrics metrics = new Metrics(cookieClicker, 11733);
        metrics.addCustomChart(new Metrics.AdvancedPie("database_type", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                Map<String, Integer> valueMap = new HashMap<>();
                if (ConfigTools.isMysql()) {
                    valueMap.put("MySQL", 1);
                } else {
                    valueMap.put("SQLite", 1);
                }
                return valueMap;
            }
        }));
        metrics.addCustomChart(new Metrics.SingleLineChart("registered_players", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return databaseManager.getRegisteredPlayerAmount();
            }
        }));
    }

    public void loadCookieClicker() {
        clickerLocations = new HashMap<>();
        ConfigUtil configUtil = new ConfigUtil("locations.yml");
        if (configUtil.getConfigurationSection("CookieClicker") == null) {
            return;
        }
        for (String currentLocation : configUtil.getConfigurationSection("CookieClicker").getKeys(false)) {
            LocationTools locationTools = new LocationTools("CookieClicker." + currentLocation);
            if (!(locationTools.isLocation())) {
                continue;
            }
            clickerLocations.put(currentLocation, locationTools.loadLocation());
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile playerProfile = playerProfiles.get(player.getUniqueId());
            if (playerProfile == null) {
                continue;
            }
            playerProfile.getPlayerManager().reloadHolograms();
        }
    }

    public TimeUpdater getTimeUpdater() {
        return timeUpdater;
    }

    public AlltimeUpdater getAlltimeUpdater() {
        return alltimeUpdater;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ResetTimerUpdater getResetTimerUpdater() {
        return resetTimerUpdater;
    }

    public void setTimeUpdater(TimeUpdater timeUpdater) {
        this.timeUpdater = timeUpdater;
    }

    public void setAlltimeUpdater(AlltimeUpdater alltimeUpdater) {
        this.alltimeUpdater = alltimeUpdater;
    }

    public void setResetTimerUpdater(ResetTimerUpdater resetTimerUpdater) {
        this.resetTimerUpdater = resetTimerUpdater;
    }

    public HashMap<String, Location> getClickerLocations() {
        return clickerLocations;
    }

    public HashMap<Location, String> getStatsWallLocations() {
        return statsWallLocations;
    }

    public HashMap<UUID, PlayerProfile> getPlayerProfiles() {
        return playerProfiles;
    }
}
