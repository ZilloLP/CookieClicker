package de.zillolp.cookieclicker.utils;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.ConfigTools;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private static final CookieClicker cookieClicker = CookieClicker.cookieClicker;
    private static final int resourceId = 65485;
    private static boolean isNewest;
    public static String version;
    public static String currentVersion;

    public static void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(cookieClicker, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                System.out.println("Unable to check for updates: " + exception.getMessage());
            }
        });
    }

    public void checkVersion() {
        if (!(ConfigTools.isUpdates())) {
            isNewest = true;
            return;
        }
        currentVersion = cookieClicker.getDescription().getVersion();
        getVersion(version -> {
            this.version = version;
            isNewest = currentVersion.equals(version);
            if (!(isNewest)) {
                System.out.println("[CookieClicker] You are using an outdated version!");
                System.out.println("[CookieClicker] Latest version: " + version + ". You are on version: " + currentVersion + ".");
                System.out.println("[CookieClicker] Update here: https://www.spigotmc.org/resources/cookieclicker.65485/");
            }
        });
    }

    public static boolean isNewest() {
        return isNewest;
    }
}
