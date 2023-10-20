package de.zillolp.cookieclicker.inventory.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.InventoryType;
import de.zillolp.cookieclicker.enums.SoundType;
import de.zillolp.cookieclicker.inventory.InventoryManager;
import de.zillolp.cookieclicker.manager.SoundManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class HomeInventoryListener extends InventoryListener {
    private final LanguageConfig languageConfig;

    public HomeInventoryListener(CookieClicker plugin, InventoryManager inventoryManager, InventoryType inventoryType) {
        super(plugin, inventoryManager, inventoryType);
        this.languageConfig = plugin.getLanguageConfig();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        SoundManager soundManager = plugin.getSoundManager();
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        Material type = currentItem.getType();
        String displayName = currentItem.getItemMeta().getDisplayName();
        if (type == Material.COOKIE && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage("DESIGN"))) {
            inventoryManager.openInventory(InventoryType.DESIGN, player);
            soundManager.playSound(player, SoundType.DESIGN);
        } else if (type == Material.PINK_TERRACOTTA && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage("SHOP"))) {
            inventoryManager.openInventory(InventoryType.SHOP, player);
            soundManager.playSound(player, SoundType.SHOP);
        }
    }
}
