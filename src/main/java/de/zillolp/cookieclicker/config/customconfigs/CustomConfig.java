package de.zillolp.cookieclicker.config.customconfigs;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class CustomConfig {
    protected final CookieClicker plugin;
    private final File file;
    protected FileConfiguration fileConfiguration;

    public CustomConfig(CookieClicker plugin, String name) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), name);
        if (!(file.exists())) {
            plugin.saveResource(name, true);
        }
        loadConfiguration();
    }

    public void loadConfiguration() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    protected void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }
}
