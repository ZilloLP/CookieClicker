package de.zillolp.cookieclicker.config.customconfigs;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.Price;
import de.zillolp.cookieclicker.prices.CustomPrice;
import de.zillolp.cookieclicker.prices.NormalPrice;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LanguageConfig extends CustomConfig {

    public LanguageConfig(CookieClicker plugin, String name) {
        super(plugin, name);
    }

    public String getTranslatedLanguage(String key) {
        if (!(fileConfiguration.contains(key))) {
            return key;
        }
        String language = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString(key));
        language = language.replace("%Ae%", "Ä");
        language = language.replace("%ae%", "ä");
        language = language.replace("%Oe%", "Ö");
        language = language.replace("%oe%", "ö");
        language = language.replace("%Ue%", "Ü");
        language = language.replace("%ue%", "ü");
        language = language.replace("%sz%", "ß");
        language = language.replace("%>%", "»");
        language = language.replace("%<%", "«");
        language = language.replace("%*%", "×");
        language = language.replace("%|%", "┃");
        language = language.replace("%->%", "➜");
        language = language.replace("%_>%", "➥");
        return language;
    }

    public String[] getTranslatedLanguages(String section) {
        ArrayList<String> languages = new ArrayList<>();
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(section);
        if (configurationSection == null) {
            return languages.toArray(new String[0]);
        }
        for (String language : configurationSection.getKeys(false)) {
            languages.add(getTranslatedLanguage(section + "." + language));
        }
        return languages.toArray(new String[0]);
    }

    public String getReplacedLanguage(String key, PlayerProfile playerProfile) {
        if (!(fileConfiguration.contains(key))) {
            return key;
        }
        String language = getTranslatedLanguage(key);
        language = language.replace("%cookies%", formatNumber(playerProfile.getCookies()));
        language = language.replace("%perclick%", formatNumber(playerProfile.getPerClick()));
        language = language.replace("%clickerclicks%", formatNumber(playerProfile.getClickerClicks()));
        return language;
    }

    public String getPriceLanguage(String key, PlayerProfile playerProfile, long priceAmount) {
        if (!(fileConfiguration.contains(key))) {
            return key;
        }
        return getReplacedLanguage(key, playerProfile).replace("%price%", formatNumber(priceAmount));
    }

    public String getPriceLanguage(String key, PlayerProfile playerProfile, Price price) {
        if (!(fileConfiguration.contains(key))) {
            return key;
        }
        long priceAmount = playerProfile.getPrice(price);
        String language = getReplacedLanguage(key, playerProfile);
        language = language.replace("%price%", formatNumber(priceAmount));
        CustomPrice customPrice = price.getCustomPrice();
        if (customPrice instanceof NormalPrice) {
            language = language.replace("%addclick%", formatNumber(((NormalPrice) customPrice).getAddClick()));
        }
        return language;
    }

    public String[] getStatsWallLanguage(String section, String place, String name, String value) {
        ArrayList<String> languages = new ArrayList<>();
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(section);
        if (configurationSection == null) {
            return languages.toArray(new String[0]);
        }
        for (String language : configurationSection.getKeys(false)) {
            String replacedLanguage = getTranslatedLanguage(section + "." + language);
            replacedLanguage = replacedLanguage.replace("%place%", place);
            replacedLanguage = replacedLanguage.replace("%name%", name);
            replacedLanguage = replacedLanguage.replace("%value%", value);
            languages.add(replacedLanguage);
        }
        return languages.toArray(new String[0]);
    }

    public String[] getStatsInfoLanguage(String section, String place, PlayerProfile playerProfile) {
        ArrayList<String> languages = new ArrayList<>();
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(section);
        if (configurationSection == null) {
            return languages.toArray(new String[0]);
        }
        for (String language : configurationSection.getKeys(false)) {
            String replacedLanguage = getTranslatedLanguage(section + "." + language);
            replacedLanguage = replacedLanguage.replace("%place%", place);
            replacedLanguage = replacedLanguage.replace("%name%", playerProfile.getName());
            replacedLanguage = replacedLanguage.replace("%cookies%", formatNumber(playerProfile.getCookies()));
            replacedLanguage = replacedLanguage.replace("%perclick%", formatNumber(playerProfile.getPerClick()));
            replacedLanguage = replacedLanguage.replace("%clickerclicks%", formatNumber(playerProfile.getClickerClicks()));
            languages.add(replacedLanguage);
        }
        return languages.toArray(new String[0]);
    }

    public String getLanguageWithPrefix(String key) {
        return getTranslatedLanguage("PREFIX") + getTranslatedLanguage(key);
    }

    public String formatTime(String language, int time) {
        int hours = (time % 86400) / 3600;
        int minutes = ((time % 86400) % 3600) / 60;
        int seconds = ((time % 86400) % 3600) % 60;
        return language.replace("%time%", String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    public String formatNumber(Long number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else {
            return new DecimalFormat("0,000").format(number);
        }
    }
}
