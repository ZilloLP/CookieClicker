package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.config.PermissionTools;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerProfile playerProfile = new PlayerProfile(player);
        playerProfile.getPlayerManager().spawnHolograms();
        cookieClicker.getPlayerProfiles().put(player.getUniqueId(), playerProfile);

        if (!(player.hasPermission(PermissionTools.getAdminPermission())) || UpdateChecker.isNewest()) {
            return;
        }
        player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cYou are using an outdated version!");
        player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7Latest version: §e" + UpdateChecker.version + "§7. You are on version: §e" + UpdateChecker.currentVersion + "§7.");
        player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7Update here: §ehttps://www.spigotmc.org/resources/cookieclicker.65485/");
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        cookieClicker.getPlayerProfiles().get(player.getUniqueId()).uploadProfile();
    }
}
