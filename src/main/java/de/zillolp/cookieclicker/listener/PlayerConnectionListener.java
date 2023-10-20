package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final CookieClicker plugin;

    public PlayerConnectionListener(CookieClicker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getCookieClickerManager().registerPlayer(player, true);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getCookieClickerManager().unregisterPlayer(player, true);
    }
}
