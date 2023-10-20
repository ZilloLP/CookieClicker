package de.zillolp.cookieclicker.enums;

public enum InventoryType {
    HOME("HOME_TITLE"),
    DESIGN("DESIGN_TITLE"),
    SHOP("SHOP_TITLE"),
    PREMIUM_SHOP("PREMIUM_SHOP_TITLE"),
    BONUS_SHOP("BONUS_SHOP_TITLE");

    private final String languageName;
    private String title;

    InventoryType(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
