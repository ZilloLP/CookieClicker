package de.zillolp.cookieclicker.enums;

import de.zillolp.cookieclicker.xclasses.XMaterial;

public enum PremiumPrices {
    SEVEN(XMaterial.BLAZE_ROD, 7, 16),
    EIGHT(XMaterial.IRON_INGOT, 8, 18),
    NINE(XMaterial.MUSIC_DISC_WARD, 9, 20),
    TEN(XMaterial.FIREWORK_STAR, 10, 22),
    ELEVEN(XMaterial.ENDER_EYE, 11, 24),
    TWELFTH(XMaterial.BLAZE_POWDER, 12, 26),
    THIRTEEN(XMaterial.NETHER_STAR, 13, 28);

    private final XMaterial type;
    private final int number;
    private final int addClick;

    PremiumPrices(XMaterial type, int number, int addClick) {
        this.type = type;
        this.number = number;
        this.addClick = addClick;
    }

    public XMaterial getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public int getAddClick() {
        return addClick;
    }
}
