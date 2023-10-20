package de.zillolp.cookieclicker.prices;

import org.bukkit.Material;

public class NormalPrice extends CustomPrice {
    private final long addClick;

    public NormalPrice(Material type, int slot, long addClick, long defaultPrice) {
        super(type, slot, defaultPrice);
        this.addClick = addClick;
    }

    public long getAddClick() {
        return addClick;
    }
}
