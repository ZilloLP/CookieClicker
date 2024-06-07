package de.zillolp.cookieclicker.statswall.statswalls;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.statswall.StatsWallManager;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;

public abstract class StatsWall implements Runnable {
    protected final CookieClicker plugin;
    protected final LanguageConfig languageConfig;
    protected final StatsWallManager statsWallManager;
    protected final HashMap<UUID, PlayerProfile> playerProfiles;
    protected final LinkedHashMap<String, Long> cachedData;
    protected final LinkedHashMap<String, Long> sortedData;
    private int taskID;
    private boolean isRunning;

    public StatsWall(CookieClicker plugin, StatsWallManager statsWallManager) {
        this.plugin = plugin;
        this.languageConfig = plugin.getLanguageConfig();
        this.statsWallManager = statsWallManager;
        this.playerProfiles = plugin.getCookieClickerManager().getPlayerProfiles();
        this.cachedData = new LinkedHashMap<>();
        this.sortedData = new LinkedHashMap<>();
    }

    @Override
    public void run() {
        rankData();
        setWalls();
    }

    public abstract void rankData();

    public abstract void setWalls();

    protected void setWall(Location[] locations, String name, String[] lines) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                Block headBlock = locations[0].getBlock();
                Block signBlock = locations[1].getBlock();
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
                if (plugin.getDatabaseManager().playerExists(name)) {
                    skull.setOwner(name);
                } else {
                    skull.setOwnerProfile(ReflectionUtil.getProfile("http://textures.minecraft.net/texture/badc048a7ce78f7dad72a07da27d85c0916881e5522eeed1e3daf217a38c1a"));
                }
                skull.update(true);
                sign.setLine(0, lines[0]);
                sign.setLine(1, lines[1]);
                sign.setLine(2, lines[2]);
                sign.setLine(3, lines[3]);
                sign.update();
            }
        });
    }

    protected String getPlace(int place) {
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        String placeLanguage;
        switch (place) {
            case 1:
                placeLanguage = languageConfig.getTranslatedLanguage("Statswall.Rank Prefix.1");
                break;
            case 2:
                placeLanguage = languageConfig.getTranslatedLanguage("Statswall.Rank Prefix.2");
                break;
            case 3:
                placeLanguage = languageConfig.getTranslatedLanguage("Statswall.Rank Prefix.3");
                break;
            default:
                placeLanguage = languageConfig.getTranslatedLanguage("Statswall.Rank Prefix.default").replace("%number%", languageConfig.formatNumber((long) place));
                break;
        }
        return placeLanguage;
    }

    public void start() {
        isRunning = true;
        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, 0, plugin.getConfig().getInt("Statswall refresh") * 20L);
    }

    public void stop() {
        isRunning = false;
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public boolean isRunning() {
        return isRunning;
    }

    protected <K, V> K getKey(LinkedHashMap<K, V> map, int index) {
        Iterator<K> iterator = map.keySet().iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    public LinkedHashMap<String, Long> getCachedData() {
        return cachedData;
    }

    public LinkedHashMap<String, Long> getSortedData() {
        return sortedData;
    }
}
