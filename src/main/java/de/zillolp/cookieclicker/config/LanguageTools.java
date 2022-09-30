package de.zillolp.cookieclicker.config;

import de.zillolp.cookieclicker.utils.ConfigUtil;
import de.zillolp.cookieclicker.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class LanguageTools {
    private static HashMap<String, String> languageMap;
    private static HashMap<String, String[]> linesMap;

    public static void load() {
        ConfigUtil configUtil = new ConfigUtil("language.yml");
        languageMap = new HashMap<>();
        linesMap = new HashMap<>();
        if (configUtil.getKeys() == null) {
            return;
        }
        String[] blackList = new String[]{"Clickerhologram", "Statswall.Rank Prefix", "Statswall.Sign Lines", "PLAYER_STATS_INFO"};
        for (String blackWord : blackList) {
            for (String currentString : configUtil.getKeys()) {
                if (currentString.startsWith("#") || currentString.startsWith(blackWord)) {
                    continue;
                }
                languageMap.put(currentString, StringUtil.replaceDefaults(configUtil.getString(currentString)));
            }
            if (configUtil.getConfigurationSection(blackWord) != null) {
                ArrayList<String> lines = new ArrayList<>();
                for (String line : configUtil.getConfigurationSection(blackWord).getKeys(false)) {
                    lines.add(StringUtil.replaceDefaults(configUtil.getString(blackWord + "." + line)));
                }
                linesMap.put(blackWord, lines.toArray(new String[lines.size()]));
            }
        }
    }

    public static String getLanguage(String field) {
        return languageMap.get(field);
    }

    public static String[] getLines(String field) {
        return linesMap.get(field);
    }

    public static String getLanguageReplaced(String field, String target, Object replace) {
        String value = String.valueOf(replace);
        if (StringUtil.isNumber(value)) {
            return getLanguage(field).replace(target, StringUtil.formatNumber(Long.valueOf(value)));
        }
        return getLanguage(field).replace(target, String.valueOf(replace));
    }

    public static String getNO_PERMISSION() {
        return getLanguage("PREFIX") + getLanguage("NO_PERMISSION");
    }

    public static String getUNKNOWN_COMMAND() {
        return getLanguage("PREFIX") + getLanguage("UNKNOWN_COMMAND");
    }

    public static String getONLY_PLAYER() {
        return getLanguage("PREFIX") + getLanguage("ONLY_PLAYER");
    }

    public static String getRank(int index) {
        index++;
        switch (index) {
            case 1:
                return getLines("Statswall.Rank Prefix")[0];
            case 2:
                return getLines("Statswall.Rank Prefix")[1];
            case 3:
                return getLines("Statswall.Rank Prefix")[2];
            default:
                return getLines("Statswall.Rank Prefix")[3].replace("%number%", String.valueOf(index));
        }
    }

    public static String[] getSTATSWALL_LINES(String name, String place, String perClick) {
        ArrayList<String> lines = new ArrayList<>();
        for (String line : getLines("Statswall.Sign Lines")) {
            line = line.replace("%name%", name);
            line = line.replace("%place%", place);
            line = line.replace("%perClick%", perClick);
            lines.add(line);
        }
        return lines.toArray(new String[lines.size()]);
    }

    public static String[] getSTATS_LINES(String field, String name, String place, String cookies, String perClick, String clickerClicks) {
        ArrayList<String> lines = new ArrayList<>();
        for (String line : getLines(field)) {
            line = line.replace("%name%", name);
            line = line.replace("%place%", place);
            line = line.replace("%cookies%", cookies);
            line = line.replace("%perClick%", perClick);
            line = line.replace("%clickerClicks%", clickerClicks);
            lines.add(line);
        }
        return lines.toArray(new String[lines.size()]);
    }
}