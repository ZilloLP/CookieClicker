package de.zillolp.cookieclicker.statswall.statswalls;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.statswall.StatsWallManager;
import org.bukkit.Location;

import java.util.Comparator;
import java.util.Map;

public class AlltimeStatsWall extends StatsWall {

    public AlltimeStatsWall(CookieClicker plugin, StatsWallManager statsWallManager) {
        super(plugin, statsWallManager);
    }

    @Override
    public void rankData() {
        sortedData.clear();
        String rankType = statsWallManager.getRankType();
        for (PlayerProfile playerProfile : playerProfiles.values()) {
            String name = playerProfile.getName();
            switch (rankType) {
                case "cookies":
                    cachedData.put(name, playerProfile.getCookies());
                    break;
                case "clickerclicks":
                    cachedData.put(name, playerProfile.getClickerClicks());
                    break;
                default:
                    cachedData.put(name, playerProfile.getPerClick());
                    break;
            }
        }
        cachedData.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(playerData -> sortedData.put(playerData.getKey(), playerData.getValue()));
    }

    @Override
    public void setWalls() {
        for (Map.Entry<Integer, Location[]> statsWall : statsWallManager.getAlltimeLocations().entrySet()) {
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
}
