package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.ConfigTools;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AntiAutoClicker implements Runnable {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    public AntiAutoClicker() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(cookieClicker, this, 0, 20);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
            if (playerProfile == null || playerProfile.getCPS() <= 0) {
                return;
            }
            playerProfile.removeCPS(ConfigTools.getMaxCPS());
            if (playerProfile.getCPS() > 0) {
                playerProfile.setOverCPS(true);
                continue;
            }
            playerProfile.setOverCPS(false);
        }
    }
}
