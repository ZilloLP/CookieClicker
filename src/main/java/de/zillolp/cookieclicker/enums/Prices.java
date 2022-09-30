package de.zillolp.cookieclicker.enums;

import de.zillolp.cookieclicker.xclasses.XMaterial;

public enum Prices {
    ONE(XMaterial.GOLDEN_HOE, 0, 2),
    TWO(XMaterial.IRON_AXE, 1, 4),
    THREE(XMaterial.DIAMOND_PICKAXE, 2, 6),
    FOUR(XMaterial.DIAMOND, 3, 8),
    FIVE(XMaterial.GOLD_BLOCK, 4, 10),
    SIX(XMaterial.IRON_SHOVEL, 5, 12),
    SEVEN(XMaterial.ENDER_PEARL, 6, 14);

    private final XMaterial type;
    private final int number;
    private final int addClick;

    Prices(XMaterial type, int number, int addClick) {
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
