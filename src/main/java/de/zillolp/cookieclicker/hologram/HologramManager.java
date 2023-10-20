package de.zillolp.cookieclicker.hologram;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.hologram.holograms.Hologram;
import de.zillolp.cookieclicker.hologram.holograms.TextHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class HologramManager {
    private final CookieClicker plugin;
    private final HashMap<UUID, HashMap<Location, Hologram>> hologramLists;

    public HologramManager(CookieClicker plugin) {
        this.plugin = plugin;
        this.hologramLists = new HashMap<>();
    }

    public void spawnHologram(Player player, Hologram hologram, Location location) {
        UUID uuid = player.getUniqueId();
        HashMap<Location, Hologram> holograms;
        if (hologramLists.containsKey(uuid)) {
            holograms = hologramLists.get(uuid);
        } else {
            holograms = new HashMap<>();
            hologramLists.put(uuid, holograms);
        }
        if (location.getWorld() != player.getWorld()) {
            return;
        }
        holograms.put(location, hologram);
        hologram.spawn(player, location);
    }

    public void spawnHolograms(Player player) {
        for (Location location : plugin.getCookieClickerManager().getClickerLocations()) {
            spawnHologram(player, new TextHologram(plugin.getLanguageConfig().getTranslatedLanguages("Clickerhologram")), location);
        }
    }

    public void deleteHologram(Player player, Location location) {
        UUID uuid = player.getUniqueId();
        if (!(hologramLists.containsKey(uuid))) {
            return;
        }
        HashMap<Location, Hologram> hologramList = hologramLists.get(uuid);
        if (!(hologramList.containsKey(location))) {
            return;
        }
        hologramList.get(location).destroy(player);
        hologramList.remove(location);
    }

    public void deleteHolograms(Player player) {
        UUID uuid = player.getUniqueId();
        if (!(hologramLists.containsKey(uuid))) {
            return;
        }
        HashMap<Location, Hologram> hologramList = hologramLists.get(uuid);
        for (Hologram hologram : hologramList.values()) {
            hologram.destroy(player);
        }
        hologramLists.remove(uuid);
    }

    public HashMap<UUID, HashMap<Location, Hologram>> getHologramLists() {
        return hologramLists;
    }
}
