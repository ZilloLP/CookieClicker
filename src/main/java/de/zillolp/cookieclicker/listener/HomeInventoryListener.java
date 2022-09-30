package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.profiles.InventoryProfile;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.InventorySetter;
import de.zillolp.cookieclicker.xclasses.XMaterial;
import de.zillolp.cookieclicker.xclasses.XSound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HomeInventoryListener implements Listener {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack currentItem = event.getCurrentItem();
        if (clickedInventory == null || currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }
        String title = event.getView().getTitle();
        if (!(title.equalsIgnoreCase(LanguageTools.getLanguage("HOME_TITLE")))) {
            return;
        }
        event.setCancelled(true);
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        if (playerProfile == null) {
            return;
        }
        Material type = currentItem.getType();
        String displayName = currentItem.getItemMeta().getDisplayName();
        SoundManager soundManager = playerProfile.getSoundManager();
        InventoryProfile inventoryProfile = playerProfile.getInventoryProfile();
        if (type == XMaterial.PINK_TERRACOTTA.parseMaterial() && displayName.equalsIgnoreCase(LanguageTools.getLanguage("SHOP"))) {
            Inventory shopInventory = inventoryProfile.getShopInventory();
            if (shopInventory == null) {
                shopInventory = inventoryProfile.setShopInventory(Bukkit.createInventory(null, 4 * 9, LanguageTools.getLanguage("SHOP_TITLE")));
            }
            InventorySetter.setShopInventory(player, shopInventory);
            player.openInventory(shopInventory);
            soundManager.playSound(XSound.BLOCK_NOTE_BLOCK_BASS);
        } else if (type == XMaterial.COOKIE.parseMaterial() && displayName.equalsIgnoreCase(LanguageTools.getLanguage("DESIGN"))) {
            Inventory designInventory = inventoryProfile.getDesignInventory();
            if (designInventory == null) {
                designInventory = inventoryProfile.setDesignInventory(Bukkit.createInventory(null, 4 * 9, LanguageTools.getLanguage("DESIGN_TITLE")));
            }
            InventorySetter.setDesignInventory(player, designInventory);
            player.openInventory(designInventory);
            soundManager.playSound(XSound.BLOCK_NOTE_BLOCK_BASS);
        }
    }
}
