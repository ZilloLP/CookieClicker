package de.zillolp.cookieclicker.enums;

import de.zillolp.cookieclicker.prices.CustomPrice;
import de.zillolp.cookieclicker.prices.NormalPrice;
import de.zillolp.cookieclicker.prices.PremiumPrice;
import org.bukkit.Material;

public enum Price {
    PRICE(new NormalPrice(Material.GOLDEN_HOE, 19, 2, 30)),
    PRICE1(new NormalPrice(Material.IRON_AXE, 20, 4, 360)),
    PRICE2(new NormalPrice(Material.DIAMOND_PICKAXE, 21, 6, 690)),
    PRICE3(new NormalPrice(Material.DIAMOND, 22, 8, 920)),
    PRICE4(new NormalPrice(Material.GOLD_BLOCK, 23, 10, 1250)),
    PRICE5(new NormalPrice(Material.IRON_SHOVEL, 24, 12, 1580)),
    PRICE6(new NormalPrice(Material.ENDER_PEARL, 25, 14, 1910)),
    PREMIUM_PRICE(new PremiumPrice(Material.BLAZE_ROD, 19, 16, 2240)),
    PREMIUM_PRICE1(new PremiumPrice(Material.IRON_INGOT, 20, 18, 2570)),
    PREMIUM_PRICE2(new PremiumPrice(Material.MUSIC_DISC_WARD, 21, 20, 2900)),
    PREMIUM_PRICE3(new PremiumPrice(Material.FIREWORK_STAR, 22, 22, 3230)),
    PREMIUM_PRICE4(new PremiumPrice(Material.ENDER_EYE, 23, 24, 3560)),
    PREMIUM_PRICE5(new PremiumPrice(Material.BLAZE_POWDER, 24, 26, 3890)),
    PREMIUM_PRICE6(new PremiumPrice(Material.NETHER_STAR, 25, 28, 4220));

    private final CustomPrice customPrice;

    Price(CustomPrice customPrice) {
        this.customPrice = customPrice;
    }

    public CustomPrice getCustomPrice() {
        return customPrice;
    }
}
