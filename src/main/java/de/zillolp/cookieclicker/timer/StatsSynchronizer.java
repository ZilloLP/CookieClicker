package de.zillolp.cookieclicker.timer;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.manager.DatabaseManager;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class StatsSynchronizer extends CustomTimer {
    public StatsSynchronizer(CookieClicker plugin, int seconds) {
        super(plugin, seconds);
    }

    @Override
    protected void tick() {
        DatabaseManager databaseManager = plugin.getDatabaseManager();
        HashMap<UUID, PlayerProfile> playerProfiles = plugin.getCookieClickerManager().getPlayerProfiles();
        for (Player player : Bukkit.getOnlinePlayers()) {
            databaseManager.uploadProfile(playerProfiles.get(player.getUniqueId()), false);
        }
    }
}
