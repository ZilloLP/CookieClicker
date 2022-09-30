package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.runnables.ResetTimerUpdater;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class AntiAFKListener implements Listener {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;
    private final HashMap<UUID, PlayerProfile> playerProfiles = cookieClicker.getPlayerProfiles();

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerProfile playerProfile = playerProfiles.get(player.getUniqueId());
        if (playerProfile == null) {
            return;
        }
        Location locationFrom = event.getFrom();
        Location locationTo = event.getTo();
        if (locationFrom.getBlockX() == locationTo.getBlockX() && locationFrom.getBlockZ() == locationTo.getBlockZ()) {
            return;
        }
        playerProfile.setLastMove(System.currentTimeMillis());
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PlayerProfile playerProfile = playerProfiles.get(player.getUniqueId());
        if (playerProfile == null) {
            return;
        }
        playerProfile.setLastMove(System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerProfile playerProfile = playerProfiles.get(uuid);
        if (playerProfile == null) {
            return;
        }
        playerProfile.getPlayerManager().reloadHolograms();
        ResetTimerUpdater.deleteTimer(uuid);
    }
}
