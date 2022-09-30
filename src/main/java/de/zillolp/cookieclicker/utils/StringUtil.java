package de.zillolp.cookieclicker.utils;

import java.text.DecimalFormat;

public class StringUtil {
    public static String formatNumber(Long number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else {
            return new DecimalFormat("0,000").format(number);
        }
    }

    public static boolean isNumber(String value) {
        try {
            int number = Integer.parseInt(value);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String replaceDefaults(String message) {
        message = message.replace("%Ae%", "Ä");
        message = message.replace("%ae%", "ä");
        message = message.replace("%Oe%", "Ö");
        message = message.replace("%oe%", "ö");
        message = message.replace("%Ue%", "Ü");
        message = message.replace("%ue%", "ü");
        message = message.replace("%sz%", "ß");
        message = message.replace("%>%", "»");
        message = message.replace("%<%", "«");
        message = message.replace("%*%", "×");
        message = message.replace("%|%", "┃");
        message = message.replace("%->%", "➜");
        message = message.replace("&", "§");
        return message;
    }
}
