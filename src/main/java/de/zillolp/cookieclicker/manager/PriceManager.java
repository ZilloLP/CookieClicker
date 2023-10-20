package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.enums.Price;
import de.zillolp.cookieclicker.prices.CustomPrice;
import de.zillolp.cookieclicker.prices.NormalPrice;
import de.zillolp.cookieclicker.prices.PremiumPrice;

import java.util.HashMap;

public class PriceManager {
    private final HashMap<Price, NormalPrice> prices;
    private final HashMap<Price, PremiumPrice> premiumPrices;

    public PriceManager() {
        this.prices = new HashMap<>();
        this.premiumPrices = new HashMap<>();
        load();
    }

    public void load() {
        for (Price price : Price.values()) {
            CustomPrice customPrice = price.getCustomPrice();
            if (customPrice.getClass().equals(NormalPrice.class)) {
                prices.put(price, (NormalPrice) customPrice);
            } else if (customPrice.getClass().equals(PremiumPrice.class)) {
                premiumPrices.put(price, (PremiumPrice) customPrice);
            }
        }
    }

    public HashMap<Price, NormalPrice> getPrices() {
        return prices;
    }

    public HashMap<Price, PremiumPrice> getPremiumPrices() {
        return premiumPrices;
    }
}
