package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.config.LocationTools;
import de.zillolp.cookieclicker.config.PermissionTools;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.xclasses.XSound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class StatsWallListener implements Listener {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        Location location = clickedBlock.getLocation();
        if (!(clickedBlock.getType() != Material.AIR && cookieClicker.getStatsWallLocations().containsKey(location))) {
            return;
        }
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        if (playerProfile == null) {
            return;
        }
        event.setCancelled(true);
        Action action = event.getAction();

        HashMap<Location, String> statsWallLocations = cookieClicker.getStatsWallLocations();
        String[] wallInfo = statsWallLocations.get(location).split("_");
        SoundManager soundManager = playerProfile.getSoundManager();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (playerProfile.getLastClick() + 250L > System.currentTimeMillis()) {
                return;
            }
            BlockState headState = clickedBlock.getState();
            if (!(wallInfo[2].equalsIgnoreCase("head") || headState instanceof Skull)) {
                return;
            }
            Skull skull = (Skull) headState;
            String playerName = skull.getOwner();
            if (playerName == null || playerName.equalsIgnoreCase("MHF_Question")) {
                return;
            }
            player.performCommand("cookieclicker stats " + playerName);
            playerProfile.setLastClick(System.currentTimeMillis());
            soundManager.playSound(XSound.BLOCK_LAVA_POP);
            return;
        }
        if (!(player.hasPermission(PermissionTools.getAdminPermission()))) {
            return;
        }
        int rank = Integer.parseInt(wallInfo[0]);
        String type = wallInfo[1];
        LocationTools locationTools = new LocationTools("StatsWalls." + type + "." + rank);
        if (!(locationTools.isLocation())) {
            return;
        }
        for (Location wallLocation : locationTools.loadLocations()) {
            statsWallLocations.remove(wallLocation);
        }
        locationTools.resetLocation();
        switch (type) {
            case "AllTime":
                cookieClicker.getAlltimeUpdater().reload();
                break;
            case "Time":
                cookieClicker.getTimeUpdater().reload();
                break;
        }
        player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7Du hast den Rang §6" + rank + " §7entfernt.");
    }
}
