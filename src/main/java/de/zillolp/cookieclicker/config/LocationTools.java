package de.zillolp.cookieclicker.config;

import de.zillolp.cookieclicker.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

public class LocationTools {
    private final ConfigUtil configUtil = new ConfigUtil("locations.yml");
    private final String root;
    private Location location;

    public LocationTools(String root) {
        this.root = root;
    }

    public LocationTools(String root, Location location) {
        this.root = root;
        this.location = location;
    }

    public boolean isLocation() {
        return configUtil.getString(root) != null;
    }

    public void saveLocation() {
        configUtil.set(root + ".World", location.getWorld().getName());
        configUtil.set(root + ".X", location.getX());
        configUtil.set(root + ".Y", location.getY());
        configUtil.set(root + ".Z", location.getZ());
        configUtil.set(root + ".Yaw", location.getYaw());
        configUtil.set(root + ".Pitch", location.getPitch());
    }

    public Location loadLocation() {
        String worldName = configUtil.getString(root + ".World");
        if (worldName == null || (!(Bukkit.getWorlds().contains(Bukkit.getWorld(worldName))))) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        double x = configUtil.getDouble(root + ".X");
        double y = configUtil.getDouble(root + ".Y");
        double z = configUtil.getDouble(root + ".Z");
        float yaw = (float) configUtil.getDouble(root + ".Yaw");
        float pitch = (float) configUtil.getDouble(root + ".Pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public ArrayList<Location> loadLocations() {
        ConfigurationSection configurationSection = configUtil.getConfigurationSection(root);
        ArrayList<Location> locations = new ArrayList();
        for (String key : configurationSection.getKeys(false)) {
            String worldName = configUtil.getString(root + "." + key + ".World");
            if (worldName == null || (!(Bukkit.getWorlds().contains(Bukkit.getWorld(worldName))))) {
                return null;
            }
            World world = Bukkit.getWorld(worldName);
            double x = configUtil.getDouble(root + "." + key + ".X");
            double y = configUtil.getDouble(root + "." + key + ".Y");
            double z = configUtil.getDouble(root + "." + key + ".Z");
            float yaw = (float) configUtil.getDouble(root + "." + key + ".Yaw");
            float pitch = (float) configUtil.getDouble(root + "." + key + ".Pitch");
            locations.add(new Location(world, x, y, z, yaw, pitch));
        }
        return locations;
    }

    public void resetLocation() {
        ConfigurationSection configurationSection = configUtil.getConfigurationSection(root);
        if (configurationSection == null) {
            return;
        }
        for (String key : configurationSection.getKeys(false)) {
            if (configUtil.getString(root + "." + key) == null) {
                continue;
            }
            configUtil.set(root + "." + key, null);
        }
        configUtil.set(root, null);
    }
}
