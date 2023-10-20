package de.zillolp.cookieclicker.prices;

import org.bukkit.Material;

public abstract class CustomPrice {
    private final Material type;
    private final int slot;
    private final long defaultPrice;

    public CustomPrice(Material type, int slot, long defaultPrice) {
        this.type = type;
        this.slot = slot;
        this.defaultPrice = defaultPrice;
    }

    public Material getType() {
        return type;
    }

    public int getSlot() {
        return slot;
    }

    public long getDefaultPrice() {
        return defaultPrice;
    }
}
