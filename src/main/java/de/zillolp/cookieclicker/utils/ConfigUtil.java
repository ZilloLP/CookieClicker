package de.zillolp.cookieclicker.utils;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ConfigUtil {
    private final File file;
    private final YamlConfiguration yamlConfiguration;

    public ConfigUtil(String path) {
        this.file = new File(CookieClicker.cookieClicker.getDataFolder() + "/" + path);
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public boolean exist() {
        return file.exists();
    }

    public void set(String path, Object value) {
        yamlConfiguration.set(path, value);
        save();
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return yamlConfiguration.getConfigurationSection(path);
    }

    public Set<String> getKeys() {
        return yamlConfiguration.getKeys(false);
    }

    public String getString(String path) {
        return yamlConfiguration.getString(path);
    }

    public int getInteger(String path) {
        return yamlConfiguration.getInt(path);
    }

    public double getDouble(String path) {
        return yamlConfiguration.getDouble(path);
    }

    public boolean getBoolean(String path) {
        return yamlConfiguration.getBoolean(path);
    }

    public boolean contains(String path) {
        return yamlConfiguration.contains(path);
    }

    private void save() {
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
