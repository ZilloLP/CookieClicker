package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.PremiumPrices;
import de.zillolp.cookieclicker.enums.Prices;
import de.zillolp.cookieclicker.events.PlayerShopBuyEvent;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.LinkedHashMap;

public class ShopBuyListener implements Listener {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    @EventHandler
    public void onPlayerShopBuyEvent(PlayerShopBuyEvent event) {
        Prices prices = event.getPrices();
        PremiumPrices premiumPrices = event.getPremiumPrices();
        PlayerProfile playerProfile = event.getPlayerProfile();

        int addClick = 0;
        int number = 0;
        if (prices != null) {
            addClick = prices.getAddClick();
            number = prices.getNumber();
        } else if (premiumPrices != null) {
            addClick = premiumPrices.getAddClick();
            number = premiumPrices.getNumber();
        }
        playerProfile.addPerClick(addClick);
        playerProfile.addBoughtClicks(addClick);
        playerProfile.removeCookies(event.getPrice());
        playerProfile.addPrice(number, 30L + 330L * number);


        LinkedHashMap<String, Long> data = cookieClicker.getAlltimeUpdater().getCachedData();
        if (data == null) {
            return;
        }
        data.put(playerProfile.getName(), playerProfile.getPerClick());
    }

}
