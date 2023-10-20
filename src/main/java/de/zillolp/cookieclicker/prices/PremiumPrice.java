package de.zillolp.cookieclicker.prices;

import org.bukkit.Material;

public class PremiumPrice extends NormalPrice {
    public PremiumPrice(Material type, int slot, long addClick, long defaultPrice) {
        super(type, slot, addClick, defaultPrice);
    }
}
