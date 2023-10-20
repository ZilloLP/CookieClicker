package de.zillolp.cookieclicker.config;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.*;

public class ConfigManager {
    private final CookieClicker cookieClicker;
    private PluginConfig pluginConfig;
    private LanguageConfig languageConfig;
    private LocationConfig locationConfig;
    private MySQLConfig mysqlConfig;
    private PermissionsConfig permissionsConfig;

    public ConfigManager(CookieClicker cookieClicker) {
        this.cookieClicker = cookieClicker;
        this.registerConfigs();
    }

    private void registerConfigs() {
        this.pluginConfig = new PluginConfig(cookieClicker, "config.yml");
        this.languageConfig = new LanguageConfig(cookieClicker, "language.yml");
        this.locationConfig = new LocationConfig(cookieClicker, "locations.yml");
        this.mysqlConfig = new MySQLConfig(cookieClicker, "mysql.yml");
        this.permissionsConfig = new PermissionsConfig(cookieClicker, "permissions.yml");
    }

    public void reloadConfigs() {
        pluginConfig.loadConfiguration();
        languageConfig.loadConfiguration();
        locationConfig.loadConfiguration();
        mysqlConfig.loadConfiguration();
        permissionsConfig.loadConfiguration();
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public LocationConfig getLocationConfig() {
        return locationConfig;
    }

    public MySQLConfig getMySQLConfig() {
        return mysqlConfig;
    }

    public PermissionsConfig getPermissionsConfig() {
        return permissionsConfig;
    }
}
