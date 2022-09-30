package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.config.LocationTools;
import de.zillolp.cookieclicker.enums.Setups;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class SetupListener implements Listener {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            return;
        }
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        if (playerProfile == null || playerProfile.getSetups() == Setups.NONE || playerProfile.getLastClick() + 250L > System.currentTimeMillis()) {
            return;
        }
        playerProfile.setLastClick(System.currentTimeMillis());
        event.setCancelled(true);
        Setups setups = playerProfile.getSetups();
        int setupNumber = playerProfile.getSetupNumber();
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            playerProfile.setSetups(Setups.NONE);
            player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cThe operation §cwas canceled!");
            return;
        }
        LocationTools locationTools;
        Material type = clickedBlock.getType();
        String typeName = type.name();
        Location location = clickedBlock.getLocation();
        switch (setups) {
            case CLICKER:
                locationTools = new LocationTools("CookieClicker.Clicker-" + setupNumber, location);
                locationTools.saveLocation();
                HashMap<String, Location> clickerLocations = cookieClicker.getClickerLocations();
                if (clickerLocations.replace("Clicker-" + setupNumber, location) == null) {
                    clickerLocations.put("Clicker-" + setupNumber, location);
                }
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    PlayerProfile playerProfile1 = cookieClicker.getPlayerProfiles().get(player1.getUniqueId());
                    playerProfile1.getPlayerManager().deleteHolograms();
                    playerProfile1.getPlayerManager().spawnHolograms();
                }
                player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7You have set the §bCookieClicker-" + setupNumber + "§7.");
                playerProfile.setSetups(Setups.NONE);
                break;
            case STATSWALL_TIME_HEAD:
                if (typeName.contains("HEAD") || typeName.contains("SKULL")) {
                    locationTools = new LocationTools("StatsWalls.Time." + setupNumber + ".head", location);
                    locationTools.saveLocation();
                    playerProfile.setSetups(Setups.STATSWALL_TIME_SIGN);
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7Make right click on a §esign§7.");
                } else {
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cMake right click on a head.");
                }
                break;
            case STATSWALL_TIME_SIGN:
                if (type.toString().contains("SIGN")) {
                    locationTools = new LocationTools("StatsWalls.Time." + setupNumber + ".sign", location);
                    locationTools.saveLocation();
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7You have set the §bplace-" + setupNumber + "§7.");
                    playerProfile.setSetups(Setups.NONE);
                } else {
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cMake right click on a sign.");
                }
                break;
            case STATSWALL_ALLTIME_HEAD:
                if (typeName.contains("HEAD") || typeName.contains("SKULL")) {
                    locationTools = new LocationTools("StatsWalls.AllTime." + setupNumber + ".head", location);
                    locationTools.saveLocation();
                    playerProfile.setSetups(Setups.STATSWALL_ALLTIME_SIGN);
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7Make right click on a §esign§7.");
                } else {
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cMake right click on a head.");
                }
                break;
            case STATSWALL_ALLTIME_SIGN:
                if (type.toString().contains("SIGN")) {
                    locationTools = new LocationTools("StatsWalls.AllTime." + setupNumber + ".sign", location);
                    locationTools.saveLocation();
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7You have set the §bplace-" + setupNumber + "§7.");
                    playerProfile.setSetups(Setups.NONE);
                } else {
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cMake right click on a sign.");
                }
                break;
        }
    }
}
