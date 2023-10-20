package de.zillolp.cookieclicker.protections;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AntiAutoClickerProtection implements Runnable {
    private final CookieClicker plugin;

    public AntiAutoClickerProtection(CookieClicker plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, 0, 20);
    }


    @Override
    public void run() {
        ProtectionManager protectionManager = plugin.getProtectionManager();
        HashMap<UUID, Integer> playerCPS = protectionManager.getPlayerCPS();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (!(playerCPS.containsKey(uuid))) {
                continue;
            }
            int newCPS = playerCPS.get(uuid) - protectionManager.getMaxCPS();
            if (newCPS <= 0) {
                playerCPS.remove(uuid);
                continue;
            }
            playerCPS.replace(uuid, newCPS);
        }
    }
}
