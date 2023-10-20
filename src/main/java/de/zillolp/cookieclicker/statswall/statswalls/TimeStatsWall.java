package de.zillolp.cookieclicker.statswall.statswalls;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.statswall.StatsWallManager;
import org.bukkit.Location;

import java.util.Comparator;
import java.util.Map;

public class TimeStatsWall extends StatsWall {

    public TimeStatsWall(CookieClicker plugin, StatsWallManager statsWallManager) {
        super(plugin, statsWallManager);
    }

    @Override
    public void rankData() {
        sortedData.clear();
        cachedData.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(playerData -> sortedData.put(playerData.getKey(), playerData.getValue()));
    }

    @Override
    public void setWalls() {
        for (Map.Entry<Integer, Location[]> statsWall : statsWallManager.getTimeLocations().entrySet()) {
            int place = statsWall.getKey();
            int index = place - 1;
            if (sortedData.size() <= index) {
                setWall(statsWall.getValue(), "MHF_Question", languageConfig.getStatsWallLanguage("Statswall.Sign Lines", getPlace(place), "?", "?"));
                continue;
            }
            String name = getKey(sortedData, index);
            setWall(statsWall.getValue(), name, languageConfig.getStatsWallLanguage("Statswall.Sign Lines", getPlace(place), name, languageConfig.formatNumber(sortedData.get(name))));
        }
    }

    public void addCachedData(String name, long value) {
        if (cachedData.containsKey(name)) {
            cachedData.replace(name, cachedData.get(name) + value);
            return;
        }
        cachedData.put(name, value);
    }
}
