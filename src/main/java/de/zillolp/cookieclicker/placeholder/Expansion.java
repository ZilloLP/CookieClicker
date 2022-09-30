package de.zillolp.cookieclicker.placeholder;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.StringUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Expansion extends PlaceholderExpansion {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "ZilloLP";
    }

    @Override
    public String getIdentifier() {
        return "cookieclicker";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        UUID uuid = player.getUniqueId();
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(uuid);

        if (playerProfile == null) {
            return "N/A";
        }
        Long cookies = playerProfile.getCookies();
        Long perClick = playerProfile.getPerClick();
        Long clickerClicks = playerProfile.getClickerClicks();

        switch (identifier.toUpperCase()) {
            case "COOKIES":
                // %cookieclicker_cookies%
                return StringUtil.formatNumber(cookies);
            case "PERCLICK":
                // %cookieclicker_perClick%
                return StringUtil.formatNumber(perClick);
            case "CLICKERCLICKS":
                // %cookieclicker_clickerClicks%
                return StringUtil.formatNumber(clickerClicks);
            case "PLACE":
                // %cookieclicker_place%
                return String.valueOf(cookieClicker.getDatabaseManager().getRank(uuid, "PER_CLICK"));
            default:
                return "N/A";
        }
    }
}
