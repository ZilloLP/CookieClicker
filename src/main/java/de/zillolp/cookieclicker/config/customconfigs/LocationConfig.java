package de.zillolp.cookieclicker.config.customconfigs;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

public class LocationConfig extends CustomConfig {
    public LocationConfig(CookieClicker plugin, String name) {
        super(plugin, name);
    }

    public boolean isLocation(String locationName) {
        return fileConfiguration.isLocation("locations." + locationName);
    }

    public Location getLocation(String locationName) {
        return fileConfiguration.getLocation("locations." + locationName);
    }

    public ArrayList<Location> getLocations(String section) {
        String root = "locations." + section;
        ArrayList<Location> locations = new ArrayList<>();
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(root);
        if (configurationSection == null) {
            return locations;
        }
        for (String locationName : configurationSection.getKeys(false)) {
            locations.add(fileConfiguration.getLocation(root + "." + locationName));
        }
        return locations;
    }

    public void saveLocation(String locationName, Location location) {
        fileConfiguration.set("locations." + locationName, location);
        save();
    }

    public void removeLocation(String locationName) {
        fileConfiguration.set("locations." + locationName, null);
        save();
    }
}
