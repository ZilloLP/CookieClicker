package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.hologram.HologramManager;
import de.zillolp.cookieclicker.inventory.InventoryManager;
import de.zillolp.cookieclicker.listener.PacketReader;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.protections.ProtectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CookieClickerManager {
    private final CookieClicker plugin;
    private final HologramManager hologramManager;
    private final InventoryManager inventoryManager;
    private final ProtectionManager protectionManager;
    private final PacketReader packetReader;
    private final HashMap<UUID, PlayerProfile> playerProfiles;
    private ArrayList<Location> clickerLocations;

    public CookieClickerManager(CookieClicker plugin) {
        this.plugin = plugin;
        this.hologramManager = plugin.getHologramManager();
        this.inventoryManager = plugin.getInventoryManager();
        this.protectionManager = plugin.getProtectionManager();
        this.packetReader = new PacketReader(plugin);
        this.loadClickerLocations();
        this.playerProfiles = new HashMap<>();
    }

    public void registerPlayer(Player player, boolean isAsync) {
        if (!(isAsync)) {
            registerPlayer(player);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                registerPlayer(player);
            }
        });
    }

    private void registerPlayer(Player player) {
        playerProfiles.put(player.getUniqueId(), new PlayerProfile(plugin, player));
        hologramManager.spawnHolograms(player);
        packetReader.inject(player);
        inventoryManager.registerCustomInventories(player);
        protectionManager.addAFK(player.getUniqueId());
    }

    public void unregisterPlayer(Player player, boolean isAsync) {
        if (!(isAsync)) {
            unregisterPlayer(player);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                unregisterPlayer(player);
            }
        });
    }

    private void unregisterPlayer(Player player) {
        playerProfiles.get(player.getUniqueId()).uploadData();
        hologramManager.deleteHolograms(player);
        packetReader.unInject(player);
        inventoryManager.unregisterInventories(player);
        protectionManager.getLastMoves().remove(player.getUniqueId());
    }

    public void loadClickerLocations() {
        this.clickerLocations = plugin.getLocationConfig().getLocations("CookieClicker");
    }

    public ArrayList<Location> getClickerLocations() {
        return clickerLocations;
    }

    public HashMap<UUID, PlayerProfile> getPlayerProfiles() {
        return playerProfiles;
    }
}
