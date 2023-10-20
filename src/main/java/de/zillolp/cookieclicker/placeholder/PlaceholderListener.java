package de.zillolp.cookieclicker.placeholder;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.StatsWallType;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.statswall.statswalls.StatsWall;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlaceholderListener extends PlaceholderExpansion {
    private final CookieClicker plugin;

    public PlaceholderListener(CookieClicker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "ZilloLP";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cookieclicker";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        PlayerProfile playerProfile = plugin.getCookieClickerManager().getPlayerProfiles().get(player.getUniqueId());
        if (playerProfile == null) {
            return "N/A";
        }
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        switch (identifier.toUpperCase()) {
            case "COOKIES":
                // %cookieclicker_cookies%
                return languageConfig.formatNumber(playerProfile.getCookies());
            case "PERCLICK":
                // %cookieclicker_perclick%
                return languageConfig.formatNumber(playerProfile.getPerClick());
            case "CLICKERCLICKS":
                // %cookieclicker_clickerclicks%
                return languageConfig.formatNumber(playerProfile.getClickerClicks());
            case "PLACE":
                // %cookieclicker_place%
                StatsWall statsWall = StatsWallType.ALLTIME.getStatsWall();
                if (!(statsWall.isRunning())) {
                    return "N/A";
                }
                long index = 0;
                for (Map.Entry<String, Long> rankEntry : statsWall.getSortedData().entrySet()) {
                    index++;
                    if (rankEntry.getKey().equalsIgnoreCase(playerProfile.getName())) {
                        break;
                    }
                }
                return languageConfig.formatNumber(index);
            default:
                return "N/A";
        }
    }
}
