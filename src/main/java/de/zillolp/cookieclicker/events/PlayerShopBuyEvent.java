package de.zillolp.cookieclicker.events;

import de.zillolp.cookieclicker.enums.PremiumPrices;
import de.zillolp.cookieclicker.enums.Prices;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerShopBuyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final PlayerProfile playerProfile;
    private final long price;
    private Prices prices;
    private PremiumPrices premiumPrices;

    public PlayerShopBuyEvent(PlayerProfile playerProfile, long price, Prices prices) {
        this.playerProfile = playerProfile;
        this.price = price;
        this.prices = prices;
    }

    public PlayerShopBuyEvent(PlayerProfile playerProfile, long price, PremiumPrices premiumPrices) {
        this.playerProfile = playerProfile;
        this.price = price;
        this.premiumPrices = premiumPrices;
    }

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    public long getPrice() {
        return price;
    }

    public Prices getPrices() {
        return prices;
    }

    public PremiumPrices getPremiumPrices() {
        return premiumPrices;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
