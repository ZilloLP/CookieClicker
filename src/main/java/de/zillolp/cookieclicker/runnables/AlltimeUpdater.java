package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.ConfigTools;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.config.LocationTools;
import de.zillolp.cookieclicker.utils.ConfigUtil;
import de.zillolp.cookieclicker.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

import java.util.*;

public class AlltimeUpdater implements Runnable {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;
    private HashMap<String, LocationTools[]> wallLocations;
    private LinkedHashMap<String, Long> cachedData;
    private int taskID;

    public AlltimeUpdater() {
        ConfigUtil configUtil = new ConfigUtil("locations.yml");
        if (configUtil.getConfigurationSection("StatsWalls.AllTime") == null || (!(ConfigTools.isRanking()))) {
            return;
        }
        wallLocations = new HashMap<>();
        for (String key : configUtil.getConfigurationSection("StatsWalls.AllTime").getKeys(false)) {
            LocationTools locationToolsHead = new LocationTools("StatsWalls.AllTime." + key + ".head");
            LocationTools locationToolsSign = new LocationTools("StatsWalls.AllTime." + key + ".sign");

            if (!(locationToolsHead.isLocation() || locationToolsSign.isLocation())) {
                continue;
            }
            wallLocations.put(key, new LocationTools[]{locationToolsHead, locationToolsSign});
            HashMap<Location, String> statsWallLocations = cookieClicker.getStatsWallLocations();
            statsWallLocations.put(locationToolsHead.loadLocation(), key + "_AllTime" + "_head");
            statsWallLocations.put(locationToolsSign.loadLocation(), key + "_AllTime" + "_sign");
        }
        int lastPlace = configUtil.getConfigurationSection("StatsWalls.AllTime").getKeys(false).size();
        cachedData = cookieClicker.getDatabaseManager().orderBy("PER_CLICK", lastPlace);

        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(cookieClicker, this, 0, ConfigTools.getStatsRefresh());
    }

    @Override
    public void run() {
        LinkedHashMap<String, Long> sortedData = new LinkedHashMap<>();

        cachedData.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedData.put(x.getKey(), x.getValue()));
        for (Map.Entry<String, LocationTools[]> statsWall : wallLocations.entrySet()) {
            int index = Integer.parseInt(statsWall.getKey()) - 1;

            if (sortedData.size() <= index) {
                setWall(statsWall.getValue()[0], statsWall.getValue()[1], "MHF_Question", LanguageTools.getSTATSWALL_LINES(LanguageTools.getRank(index), "?", "?"));
                continue;
            }
            String name = getKey(sortedData, index);
            setWall(statsWall.getValue()[0], statsWall.getValue()[1], name, LanguageTools.getSTATSWALL_LINES(LanguageTools.getRank(index), name, StringUtil.formatNumber(sortedData.get(name))));
        }
    }

    private void setWall(LocationTools locationToolsHead, LocationTools locationToolsSign, String name, String[] lines) {
        Bukkit.getScheduler().runTask(cookieClicker, new Runnable() {
            @Override
            public void run() {
                Block headBlock = locationToolsHead.loadLocation().getBlock();
                Block signBlock = locationToolsSign.loadLocation().getBlock();

                if (headBlock.getType() == Material.AIR || signBlock.getType() == Material.AIR) {
                    return;
                }
                BlockState headState = headBlock.getState();
                BlockState signState = signBlock.getState();

                if (!(headState instanceof Skull || signState instanceof Sign)) {
                    return;
                }
                Skull skull = (Skull) headState;
                Sign sign = (Sign) signState;
                skull.setOwner(name);
                skull.update();

                sign.setLine(0, lines[0]);
                sign.setLine(1, lines[1]);
                sign.setLine(2, lines[2]);
                sign.setLine(3, lines[3]);
                sign.update();
            }
        });
    }

    <K, V> K getKey(LinkedHashMap<K, V> map, int index) {
        Iterator<K> iterator = map.keySet().iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    public LinkedHashMap<String, Long> getCachedData() {
        if (cachedData == null) {
            return new LinkedHashMap<>();
        }
        return cachedData;
    }

    public void reload() {
        cookieClicker.getAlltimeUpdater().stop();
        cookieClicker.setAlltimeUpdater(this);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
