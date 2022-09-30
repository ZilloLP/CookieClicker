package de.zillolp.cookieclicker.profiles;

import org.bukkit.inventory.Inventory;

public class InventoryProfile {
    private Inventory homeInventory;
    private Inventory shopInventory;
    private Inventory premiumShopInventory;
    private Inventory designInventory;

    public Inventory getHomeInventory() {
        return homeInventory;
    }

    public Inventory getShopInventory() {
        return shopInventory;
    }

    public Inventory getPremiumShopInventory() {
        return premiumShopInventory;
    }

    public Inventory getDesignInventory() {
        return designInventory;
    }

    public Inventory setHomeInventory(Inventory homeInventory) {
        this.homeInventory = homeInventory;
        return homeInventory;
    }

    public Inventory setShopInventory(Inventory shopInventory) {
        this.shopInventory = shopInventory;
        return shopInventory;
    }

    public Inventory setPremiumShopInventory(Inventory premiumShopInventory) {
        this.premiumShopInventory = premiumShopInventory;
        return premiumShopInventory;
    }

    public Inventory setDesignInventory(Inventory designInventory) {
        this.designInventory = designInventory;
        return designInventory;
    }
}
