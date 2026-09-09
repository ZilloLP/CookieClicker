package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.LocationConfig;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.holograms.TextHologram;
import de.zillolp.cookieclicker.manager.CookieClickerManager;
import de.zillolp.cookieclicker.manager.DesignManager;
import de.zillolp.cookieclicker.manager.HologramManager;
import de.zillolp.cookieclicker.profiles.ClickerGameProfile;
import de.zillolp.cookieclicker.runnables.StatsWallUpdater;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class SetupListener implements Listener {
    private final CookieClicker plugin;
    private final ReflectionUtil reflectionUtil;
    private final LanguageConfig languageConfig;
    private final CookieClickerManager cookieClickerManager;
    private final DesignManager designManager;
    private final HologramManager hologramManager;

    public SetupListener(CookieClicker plugin) {
        this.plugin = plugin;
        reflectionUtil = plugin.getReflectionUtil();
        languageConfig = plugin.getLanguageConfig();
        cookieClickerManager = plugin.getCookieClickerManager();
        designManager = plugin.getDesignManager();
        hologramManager = plugin.getHologramManager();
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getHand() != EquipmentSlot.HAND || clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            return;
        }
        Player player = event.getPlayer();
        ClickerGameProfile clickerGameProfile = plugin.getClickerPlayerManager().getGameProfile(player.getUniqueId());
        ClickerGameProfile.SetupState setupState = clickerGameProfile.getSetupState();
        if (setupState == ClickerGameProfile.SetupState.NONE) {
            return;
        }
        event.setCancelled(true);
        String PREFIX = languageConfig.getTranslatedLanguage(PluginLanguage.PREFIX);
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            clickerGameProfile.setSetupState(ClickerGameProfile.SetupState.NONE, 0);
            player.sendMessage(PREFIX + "§cThe operation was canceled!");
            return;
        }
        handleSetup(player, clickerGameProfile, clickedBlock);
    }

    private void handleSetup(Player player, ClickerGameProfile clickerGameProfile, Block clickedBlock) {
        ClickerGameProfile.SetupState setupState = clickerGameProfile.getSetupState();
        int number = setupState.getNumber();
        Material type = clickedBlock.getType();
        Location location = clickedBlock.getLocation();
        String PREFIX = languageConfig.getTranslatedLanguage(PluginLanguage.PREFIX);
        LocationConfig locationConfig = plugin.getLocationConfig();
        switch (setupState) {
            case SET_CLICKER:
                boolean locationFound = plugin.getCookieClickerManager().getClickerLocations().stream().anyMatch(location1 -> reflectionUtil.isSameLocation(location1, location));
                if (locationFound) {
                    player.sendMessage(PREFIX + "§cThis block is already a CookieClicker.");
                    return;
                }
                locationConfig.saveLocation("CookieClicker.Clicker-" + number, location);
                plugin.getCookieClickerManager().getClickerLocations().add(location);
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    hologramManager.spawnHologram(player1, new TextHologram(plugin, languageConfig.getReplaceLanguages(PluginLanguage.CLICKER_HOLOGRAM, player1.getUniqueId())), location.clone().add(0.5, 1, 0.5));
                    designManager.sendClickerBlockDesign(player1);
                }
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> designManager.sendClickerBlockDesign(player), 1L);
                clickerGameProfile.setSetupState(ClickerGameProfile.SetupState.NONE, 0);
                player.sendMessage(PREFIX + "§7You have set the §bCookieClicker-" + number + "§7.");
                break;
            case SET_ALLTIME_HEAD:
            case SET_TIME_HEAD:
                if (type != Material.PLAYER_HEAD && type != Material.PLAYER_WALL_HEAD) {
                    player.sendMessage(PREFIX + "§cMake right click on a head.");
                    return;
                }
                if (setupState == ClickerGameProfile.SetupState.SET_ALLTIME_HEAD) {
                    clickerGameProfile.setSetupState(ClickerGameProfile.SetupState.SET_ALLTIME_SIGN, number);
                } else {
                    clickerGameProfile.setSetupState(ClickerGameProfile.SetupState.SET_TIME_SIGN, number);
                }
                locationConfig.saveLocation(getStatsPath(setupState) + number + ".head", location);
                player.sendMessage(PREFIX + "§7Make right click on a §esign§7.");
                break;
            case SET_ALLTIME_SIGN:
            case SET_TIME_SIGN:
                if (!(clickedBlock.getState() instanceof Sign)) {
                    player.sendMessage(PREFIX + "§cMake right click on a sign.");
                    return;
                }
                locationConfig.saveLocation(getStatsPath(setupState) + number + ".sign", location);
                clickerGameProfile.setSetupState(ClickerGameProfile.SetupState.NONE, 0);
                cookieClickerManager.loadStatsWallLocations();
                StatsWallUpdater statsWallUpdater = plugin.getStatsWallUpdater();
                if (setupState == ClickerGameProfile.SetupState.SET_ALLTIME_SIGN) {
                    statsWallUpdater.getSortedAlltimeData().clear();
                    statsWallUpdater.updateAlltimeStatsWall();
                } else {
                    statsWallUpdater.getSortedTimedData().clear();
                    statsWallUpdater.updateTimeStatsWall();
                }
                player.sendMessage(PREFIX + "§7You have set the §bplace-" + number + "§7.");
                break;
        }
    }

    private String getStatsPath(ClickerGameProfile.SetupState setupState) {
        if (setupState == ClickerGameProfile.SetupState.SET_ALLTIME_HEAD || setupState == ClickerGameProfile.SetupState.SET_ALLTIME_SIGN) {
            return "StatsWalls.Alltime.";
        }
        return "StatsWalls.Time.";
    }
}
