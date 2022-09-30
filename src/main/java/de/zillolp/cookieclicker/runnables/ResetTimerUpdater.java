package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.ConfigTools;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.config.LocationTools;
import de.zillolp.cookieclicker.utils.HologramUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ResetTimerUpdater implements Runnable {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;
    private int taskID;
    private int time;
    private final LocationTools locationTools;
    private static HashMap<UUID, HologramUtil> resetTimers;

    public ResetTimerUpdater() {
        time = ConfigTools.getResetTimer();
        resetTimers = new HashMap<>();
        locationTools = new LocationTools("ResetTimer");
        if (!(locationTools.isLocation()) || (!(ConfigTools.isTimeRanking()))) {
            return;
        }
        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(cookieClicker, this, 0, 20);
    }

    @Override
    public void run() {
        if (time == 0) {
            cookieClicker.getTimeUpdater().cachedData.clear();
            time = ConfigTools.getResetTimer();
        }
        time--;

        int hours = (time % 86400) / 3600;
        int minutes = ((time % 86400) % 3600) / 60;
        int seconds = ((time % 86400) % 3600) % 60;

        String timerName = LanguageTools.getLanguageReplaced("Resettimer", "%time%", String.format("%02d:%02d:%02d", hours, minutes, seconds));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (resetTimers.containsKey(player.getUniqueId())) {
                HologramUtil hologramUtil = resetTimers.get(player.getUniqueId());
                hologramUtil.setCustomName(timerName);
                continue;
            }
            HologramUtil hologramUtil = new HologramUtil(player, timerName, locationTools.loadLocation());
            hologramUtil.spawn();
            resetTimers.put(player.getUniqueId(), hologramUtil);
        }
    }

    public static void deleteTimer(UUID uuid) {
        if (resetTimers.containsKey(uuid)) {
            resetTimers.get(uuid).delete();
            resetTimers.remove(uuid);
        }
    }

    public void reload() {
        cookieClicker.getResetTimerUpdater().stop();
        cookieClicker.setResetTimerUpdater(this);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
