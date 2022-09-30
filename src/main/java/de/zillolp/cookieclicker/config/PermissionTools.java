package de.zillolp.cookieclicker.config;

import de.zillolp.cookieclicker.utils.ConfigUtil;

public class PermissionTools {
    private static String ADMIN_PERMISSION;
    private static String PREMIUM_PERMISSION;

    public static void load() {
        ConfigUtil configUtil = new ConfigUtil("permissions.yml");
        ADMIN_PERMISSION = configUtil.getString("ADMIN_PERMISSION");
        PREMIUM_PERMISSION = configUtil.getString("PREMIUM_PERMISSION");
    }

    public static String getAdminPermission() {
        return ADMIN_PERMISSION;
    }

    public static String getPremiumPermission() {
        return PREMIUM_PERMISSION;
    }
}
