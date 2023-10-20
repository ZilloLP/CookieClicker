package de.zillolp.cookieclicker.config.customconfigs;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.command.CommandSender;

public class PermissionsConfig extends CustomConfig {
    public PermissionsConfig(CookieClicker plugin, String name) {
        super(plugin, name);
    }

    public boolean hasPermission(CommandSender sender, String key) {
        if (!(fileConfiguration.contains(key))) {
            return false;
        }
        return sender.hasPermission(fileConfiguration.getString(key));
    }
}
