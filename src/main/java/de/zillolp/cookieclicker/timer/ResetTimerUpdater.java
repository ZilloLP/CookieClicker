package de.zillolp.cookieclicker.timer;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.StatsWallType;
import de.zillolp.cookieclicker.hologram.HologramManager;
import de.zillolp.cookieclicker.hologram.holograms.Hologram;
import de.zillolp.cookieclicker.hologram.holograms.LineHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ResetTimerUpdater extends CustomTimer {
    private final int defaultTime;
    private int time;

    public ResetTimerUpdater(CookieClicker plugin) {
        super(plugin, 1);
        this.defaultTime = plugin.getPluginConfig().getFileConfiguration().getInt("Resettimer") * 60;
        time = defaultTime;
    }

    @Override
    public void tick() {
        time--;
        if (time == 0) {
            StatsWallType.TIME.getStatsWall().getCachedData().clear();
            time = defaultTime;
        }
        HologramManager hologramManager = plugin.getHologramManager();
        Location resetTimerLocation = plugin.getLocationConfig().getLocation("ResetTimer");
        HashMap<UUID, HashMap<Location, Hologram>> hologramLists = hologramManager.getHologramLists();
        if (resetTimerLocation == null || hologramLists == null) {
            return;
        }
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        String timerName = languageConfig.formatTime(languageConfig.getTranslatedLanguage("Resettimer"), time);
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (!(hologramLists.containsKey(uuid))) {
                continue;
            }
            HashMap<Location, Hologram> hologramList = hologramLists.get(uuid);
            if (hologramList.containsKey(resetTimerLocation)) {
                ((LineHologram) hologramList.get(resetTimerLocation)).changeLine(player, timerName);
                continue;
            }
            hologramManager.spawnHologram(player, new LineHologram(timerName), resetTimerLocation);
        }
    }

    public int getTime() {
        return time;
    }
}
