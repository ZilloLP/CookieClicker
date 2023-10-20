package de.zillolp.cookieclicker.statswall;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.StatsWallType;
import de.zillolp.cookieclicker.statswall.statswalls.AlltimeStatsWall;
import de.zillolp.cookieclicker.statswall.statswalls.TimeStatsWall;
import de.zillolp.cookieclicker.timer.ResetTimerUpdater;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class StatsWallManager {
    private final CookieClicker plugin;
    private final HashMap<Location, String> locationPaths;
    private final LinkedHashMap<Integer, Location[]> timeLocations;
    private final LinkedHashMap<Integer, Location[]> alltimeLocations;
    private String rankType;
    private ResetTimerUpdater resetTimerUpdater;

    public StatsWallManager(CookieClicker plugin) {
        this.plugin = plugin;
        FileConfiguration fileConfiguration = plugin.getPluginConfig().getFileConfiguration();
        this.rankType = fileConfiguration.getString("Statswall ranktype");
        if (rankType == null) {
            rankType = "perclick";
        }
        this.locationPaths = new HashMap<>();
        this.timeLocations = new LinkedHashMap<>();
        this.alltimeLocations = new LinkedHashMap<>();
        loadLocations(StatsWallType.ALLTIME);
        loadLocations(StatsWallType.TIME);
        StatsWallType.ALLTIME.setStatsWall(new AlltimeStatsWall(plugin, this));
        StatsWallType.TIME.setStatsWall(new TimeStatsWall(plugin, this));
        if (!(fileConfiguration.getBoolean("Ranking"))) {
            return;
        }
        for (StatsWallType statsWallType : StatsWallType.values()) {
            if (statsWallType == StatsWallType.TIME) {
                if (!(fileConfiguration.getBoolean("Time ranking"))) {
                    continue;
                }
                resetTimerUpdater = new ResetTimerUpdater(plugin);
            }
            statsWallType.getStatsWall().start();
        }
    }

    public void stop() {
        for (StatsWallType statsWallType : StatsWallType.values()) {
            statsWallType.getStatsWall().stop();
        }
        if (resetTimerUpdater != null) {
            resetTimerUpdater.stop();
        }
    }

    private void loadLocations(StatsWallType statsWallType) {
        String path = statsWallType.getPath();
        FileConfiguration fileConfiguration = plugin.getLocationConfig().getFileConfiguration();
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(path);
        if (configurationSection == null) {
            return;
        }
        for (String locationName : configurationSection.getKeys(false)) {
            String root = path + "." + locationName;
            Location headLocation = fileConfiguration.getLocation(root + ".head");
            Location signLocation = fileConfiguration.getLocation(root + ".sign");
            locationPaths.put(headLocation, root);
            locationPaths.put(signLocation, root);
            switch (statsWallType) {
                case TIME:
                    timeLocations.put(Integer.parseInt(locationName), new Location[]{headLocation, signLocation});
                    break;
                case ALLTIME:
                    alltimeLocations.put(Integer.parseInt(locationName), new Location[]{headLocation, signLocation});
                    break;
            }
        }
    }

    public String getRankType() {
        return rankType.toLowerCase();
    }

    public HashMap<Location, String> getLocationPaths() {
        return locationPaths;
    }

    public LinkedHashMap<Integer, Location[]> getTimeLocations() {
        return timeLocations;
    }

    public LinkedHashMap<Integer, Location[]> getAlltimeLocations() {
        return alltimeLocations;
    }
}
