package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.LocationConfig;
import de.zillolp.cookieclicker.enums.SoundType;
import de.zillolp.cookieclicker.enums.StatsWallType;
import de.zillolp.cookieclicker.statswall.StatsWallManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;

public class StatsWallListener implements Listener {
    private final CookieClicker plugin;
    private final LanguageConfig languageConfig;

    public StatsWallListener(CookieClicker plugin) {
        this.plugin = plugin;
        this.languageConfig = plugin.getLanguageConfig();
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Material type = clickedBlock.getType();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || type != Material.PLAYER_HEAD && type != Material.PLAYER_WALL_HEAD) {
            return;
        }
        Location location = clickedBlock.getLocation();
        if (!(plugin.getStatsWallManager().getLocationPaths().containsKey(location))) {
            return;
        }
        event.setCancelled(true);
        Skull skull = (Skull) clickedBlock.getState();
        String name = skull.getOwner();
        if (name == null || name.equalsIgnoreCase("MHF_Question")) {
            return;
        }
        Player player = event.getPlayer();
        player.performCommand("cookieclicker stats " + name);
        plugin.getSoundManager().playSound(player, SoundType.STATS_INFO);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        if ((!(type.name().contains("_SIGN"))) && type != Material.PLAYER_HEAD && type != Material.PLAYER_WALL_HEAD) {
            return;
        }
        Location location = block.getLocation();
        StatsWallManager statsWallManager = plugin.getStatsWallManager();
        HashMap<Location, String> locationPaths = statsWallManager.getLocationPaths();
        if (!(locationPaths.containsKey(location))) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!(plugin.getPermissionsConfig().hasPermission(player, "ADMIN_PERMISSION"))) {
            player.sendMessage(languageConfig.getLanguageWithPrefix("NO_PERMISSION"));
            return;
        }
        String root = locationPaths.get(location);
        String[] roots = root.replace(".", "_").split("_");
        String path = root.replace("locations.", "");
        LocationConfig locationConfig = plugin.getLocationConfig();
        locationPaths.remove(locationConfig.getLocation(path + ".head"));
        locationPaths.remove(locationConfig.getLocation(path + ".sign"));
        locationConfig.removeLocation(path);
        int place = Integer.parseInt(roots[3]);
        StatsWallType statsWallType = StatsWallType.valueOf(roots[2].toUpperCase());
        switch (statsWallType) {
            case TIME:
                statsWallManager.getTimeLocations().remove(place);
                break;
            case ALLTIME:
                statsWallManager.getAlltimeLocations().remove(place);
                break;
        }
        player.sendMessage(languageConfig.getTranslatedLanguage("PREFIX") + "§7You have removed §6" + statsWallType.getDisplayName() + "-" + place + "§7.");
    }
}
