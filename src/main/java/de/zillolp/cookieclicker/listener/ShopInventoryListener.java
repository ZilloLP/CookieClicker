package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.config.PermissionTools;
import de.zillolp.cookieclicker.enums.Prices;
import de.zillolp.cookieclicker.events.PlayerShopBuyEvent;
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

public class ShopInventoryListener implements Listener {
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
        if (!(title.equalsIgnoreCase(LanguageTools.getLanguage("SHOP_TITLE")))) {
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
        if (type == XMaterial.PLAYER_HEAD.parseMaterial()) {
            if (displayName.equalsIgnoreCase(LanguageTools.getLanguage("BACK"))) {
                InventoryProfile inventoryProfile = playerProfile.getInventoryProfile();
                Inventory homeInventory = inventoryProfile.getHomeInventory();
                if (homeInventory == null) {
                    homeInventory = inventoryProfile.setHomeInventory(Bukkit.createInventory(null, 5 * 9, LanguageTools.getLanguage("HOME_TITLE")));
                }
                InventorySetter.setHomeInventory(player, homeInventory);
                player.openInventory(homeInventory);
                soundManager.playSound(XSound.BLOCK_NOTE_BLOCK_SNARE);
                return;
            } else if (displayName.equalsIgnoreCase(LanguageTools.getLanguage("PREMIUM_PAGE"))) {
                if (!(player.hasPermission(PermissionTools.getPremiumPermission()))) {
                    player.sendMessage(LanguageTools.getNO_PERMISSION());
                    soundManager.playSound(XSound.BLOCK_ANVIL_BREAK);
                    return;
                }
                InventoryProfile inventoryProfile = playerProfile.getInventoryProfile();
                Inventory premiumShopInventory = inventoryProfile.getPremiumShopInventory();
                if (premiumShopInventory == null) {
                    premiumShopInventory = inventoryProfile.setPremiumShopInventory(Bukkit.createInventory(null, 4 * 9, LanguageTools.getLanguage("PREMIUM_SHOP_TITLE")));
                }
                InventorySetter.setPremiumShopInventory(player, premiumShopInventory);
                player.openInventory(premiumShopInventory);
                soundManager.playSound(XSound.BLOCK_NOTE_BLOCK_BASS);
                return;
            }
        }
        for (Prices prices : Prices.values()) {
            if (prices.getType().parseMaterial() != type) {
                continue;
            }
            long price = playerProfile.getPrice(prices.getNumber());
            if (displayName.equalsIgnoreCase(LanguageTools.getLanguageReplaced("PRICE_NOT_BUYABLE", "%price%", price))) {
                player.sendMessage(LanguageTools.getLanguage("PREFIX") + LanguageTools.getLanguage("MESSAGE_NOT_BUYABLE"));
                soundManager.playSound(XSound.BLOCK_ANVIL_BREAK);
                break;
            }
            if (!(displayName.equalsIgnoreCase(LanguageTools.getLanguageReplaced("PRICE_BUYABLE", "%price%", price)))) {
                continue;
            }
            Bukkit.getServer().getPluginManager().callEvent(new PlayerShopBuyEvent(playerProfile, price, prices));
            InventorySetter.setShopInventory(player, playerProfile.getInventoryProfile().getShopInventory());
            soundManager.playSound(XSound.ENTITY_PLAYER_LEVELUP);
            break;
        }
    }
}
