package de.zillolp.cookieclicker.inventory;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.Design;
import de.zillolp.cookieclicker.enums.InventoryType;
import de.zillolp.cookieclicker.inventory.inventorys.*;
import de.zillolp.cookieclicker.inventory.listener.DesignInventoryListener;
import de.zillolp.cookieclicker.inventory.listener.HomeInventoryListener;
import de.zillolp.cookieclicker.inventory.listener.PremiumShopInventoryListener;
import de.zillolp.cookieclicker.inventory.listener.ShopInventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {
    private final CookieClicker plugin;
    private final LanguageConfig languageConfig;
    private final HashMap<UUID, Long> clickCoolDowns;
    private final HashMap<UUID, HashMap<InventoryType, CustomInventory>> customInventories;

    public InventoryManager(CookieClicker plugin) {
        this.plugin = plugin;
        this.languageConfig = plugin.getLanguageConfig();
        this.clickCoolDowns = new HashMap<>();
        this.customInventories = new HashMap<>();
        loadInventoryLanguage();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new DesignInventoryListener(plugin, this, InventoryType.DESIGN), plugin);
        pluginManager.registerEvents(new HomeInventoryListener(plugin, this, InventoryType.HOME), plugin);
        pluginManager.registerEvents(new PremiumShopInventoryListener(plugin, this, InventoryType.PREMIUM_SHOP), plugin);
        pluginManager.registerEvents(new ShopInventoryListener(plugin, this, InventoryType.SHOP), plugin);
    }

    public void loadInventoryLanguage() {
        for (Design design : Design.values()) {
            design.load(languageConfig);
        }
        for (InventoryType inventoryType : InventoryType.values()) {
            inventoryType.setTitle(languageConfig.getTranslatedLanguage(inventoryType.getLanguageName()));
        }
    }

    public void registerCustomInventories(Player player) {
        HashMap<InventoryType, CustomInventory> inventories = new HashMap<>();
        inventories.put(InventoryType.HOME, new HomeInventory(plugin, player, InventoryType.HOME.getTitle(), 5 * 9));
        inventories.put(InventoryType.DESIGN, new DesignInventory(plugin, player, InventoryType.DESIGN.getTitle(), 4 * 9));
        inventories.put(InventoryType.PREMIUM_SHOP, new PremiumShopInventory(plugin, player, InventoryType.PREMIUM_SHOP.getTitle(), 4 * 9));
        inventories.put(InventoryType.SHOP, new ShopInventory(plugin, player, InventoryType.SHOP.getTitle(), 4 * 9));

        UUID uuid = player.getUniqueId();
        customInventories.put(uuid, inventories);
        clickCoolDowns.put(uuid, System.currentTimeMillis());
    }

    public void unregisterInventories(Player player) {
        if (!(customInventories.containsKey(player.getUniqueId()))) {
            return;
        }
        UUID uuid = player.getUniqueId();
        customInventories.remove(uuid);
        clickCoolDowns.remove(uuid);
    }

    public void openInventory(InventoryType inventoryType, Player player) {
        if (!(customInventories.containsKey(player.getUniqueId()))) {
            return;
        }
        customInventories.get(player.getUniqueId()).get(inventoryType).openInventory(player);
    }

    public void updateCoolDown(UUID uuid) {
        if (!(clickCoolDowns.containsKey(uuid))) {
            return;
        }
        clickCoolDowns.replace(uuid, System.currentTimeMillis());
    }

    public boolean hasCoolDown(UUID uuid) {
        if (!(clickCoolDowns.containsKey(uuid))) {
            return true;
        }
        return (clickCoolDowns.get(uuid) + 63L) > System.currentTimeMillis();
    }

    public HashMap<UUID, HashMap<InventoryType, CustomInventory>> getCustomInventories() {
        return customInventories;
    }
}
