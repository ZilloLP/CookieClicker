package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.Setup;
import de.zillolp.cookieclicker.hologram.HologramManager;
import de.zillolp.cookieclicker.hologram.holograms.TextHologram;
import de.zillolp.cookieclicker.manager.CookieClickerManager;
import de.zillolp.cookieclicker.manager.SetupManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SetupListener implements Listener {
    private final CookieClicker plugin;
    private final LanguageConfig languageConfig;

    public SetupListener(CookieClicker plugin) {
        this.plugin = plugin;
        this.languageConfig = plugin.getLanguageConfig();
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getHand() != EquipmentSlot.HAND || clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        SetupManager setupManager = plugin.getSetupManager();
        HashMap<UUID, Setup> setups = setupManager.getSetups();
        if (!(setups.containsKey(uuid))) {
            return;
        }
        event.setCancelled(true);
        Setup setup = setups.get(uuid);
        String PREFIX = languageConfig.getTranslatedLanguage("PREFIX");
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            setups.remove(uuid);
            player.sendMessage(PREFIX + "§cThe operation §cwas canceled!");
            return;
        }
        Material type = clickedBlock.getType();
        Location location = clickedBlock.getLocation();
        if (setup == Setup.CLICKER) {
            CookieClickerManager cookieClickerManager = plugin.getCookieClickerManager();
            ArrayList<Location> clickerLocations = cookieClickerManager.getClickerLocations();
            if (clickerLocations.contains(location)) {
                player.sendMessage(PREFIX + "§cThis block is already a CookieClicker.");
                return;
            }
            HologramManager hologramManager = plugin.getHologramManager();
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                for (Location location1 : clickerLocations) {
                    hologramManager.deleteHologram(player1, location1);
                }
            }
            setupManager.saveLocation(uuid, location);
            setups.remove(uuid);
            cookieClickerManager.loadClickerLocations();
            String[] clickerHologram = languageConfig.getTranslatedLanguages("Clickerhologram");
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                for (Location location1 : cookieClickerManager.getClickerLocations()) {
                    hologramManager.spawnHologram(player1, new TextHologram(clickerHologram), location1);
                }
            }
            player.sendMessage(PREFIX + "§7You have set the §bCookieClicker-" + setup.getSetupNumber() + "§7.");
            return;
        }
        if (setup.hasSign()) {
            if (type != Material.PLAYER_HEAD && type != Material.PLAYER_WALL_HEAD) {
                player.sendMessage(PREFIX + "§cMake right click on a head.");
                return;
            }
            setupManager.saveLocation(uuid, location);
            setup.setSign(false);
            player.sendMessage(PREFIX + "§7Make right click on a §esign§7.");
            return;
        }
        if (!(type.name().contains("_SIGN"))) {
            player.sendMessage(PREFIX + "§cMake right click on a sign.");
            return;
        }
        setupManager.saveLocation(uuid, location);
        setups.remove(uuid);
        player.sendMessage(PREFIX + "§7You have set the §bplace-" + setup.getSetupNumber() + "§7.");
    }
}
