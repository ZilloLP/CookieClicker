package de.zillolp.cookieclicker.config;

import de.zillolp.cookieclicker.utils.ConfigUtil;

public class ConfigTools {
    private static boolean mysql;
    private static boolean updates;
    private static boolean sound;
    private static boolean ranking;
    private static boolean timeRanking;
    private static int resetTimer;
    private static int statsRefresh;
    private static int maxCPS;
    private static int afkCoolDown;

    public static void load() {
        ConfigUtil configUtil = new ConfigUtil("config.yml");
        mysql = configUtil.getBoolean("MySQL");
        updates = configUtil.getBoolean("Check for updates");
        sound = configUtil.getBoolean("Sound");
        ranking = configUtil.getBoolean("Ranking");
        timeRanking = configUtil.getBoolean("Time ranking");
        resetTimer = configUtil.getInteger("Resettimer");
        statsRefresh = configUtil.getInteger("Statswall refresh");
        maxCPS = configUtil.getInteger("Maximum CPS");
        afkCoolDown = configUtil.getInteger("AFK Cooldown Seconds");
    }

    public static boolean isMysql() {
        return mysql;
    }

    public static boolean isUpdates() {
        return updates;
    }

    public static boolean isSound() {
        return sound;
    }

    public static boolean isRanking() {
        return ranking;
    }

    public static boolean isTimeRanking() {
        return timeRanking;
    }

    public static int getResetTimer() {
        return resetTimer * 60;
    }

    public static int getStatsRefresh() {
        return statsRefresh * 20;
    }

    public static int getMaxCPS() {
        return maxCPS;
    }

    public static int getAfkCoolDown() {
        return afkCoolDown * 1000;
    }
}
