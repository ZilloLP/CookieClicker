package de.zillolp.cookieclicker.config;

import de.zillolp.cookieclicker.utils.ConfigUtil;

public class MySQLTools {
    private static String host;
    private static String port;
    private static String database;
    private static String user;
    private static String password;

    public static void load() {
        ConfigUtil configUtil = new ConfigUtil("mysql.yml");
        host = configUtil.getString("Host");
        port = configUtil.getString("Port");
        database = configUtil.getString("Database");
        user = configUtil.getString("User");
        password = configUtil.getString("Password");
    }

    public static String getHost() {
        return host;
    }

    public static String getPort() {
        return port;
    }

    public static String getDatabase() {
        return database;
    }

    public static String getPassword() {
        return password;
    }

    public static String getUser() {
        return user;
    }
}
