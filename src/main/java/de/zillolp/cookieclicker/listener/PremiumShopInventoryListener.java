package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.enums.PremiumPrices;
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

public class PremiumShopInventoryListener implements Listener {
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
        if (!(title.equalsIgnoreCase(LanguageTools.getLanguage("PREMIUM_SHOP_TITLE")))) {
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
        if (type == XMaterial.PLAYER_HEAD.parseMaterial() && displayName.equalsIgnoreCase(LanguageTools.getLanguage("BACK"))) {
            InventoryProfile inventoryProfile = playerProfile.getInventoryProfile();
            Inventory shopInventory = inventoryProfile.getShopInventory();
            if (shopInventory == null) {
                shopInventory = inventoryProfile.setShopInventory(Bukkit.createInventory(null, 4 * 9, LanguageTools.getLanguage("SHOP_TITLE")));
            }
            InventorySetter.setShopInventory(player, shopInventory);
            player.openInventory(shopInventory);
            soundManager.playSound(XSound.BLOCK_NOTE_BLOCK_SNARE);
            return;
        }
        for (PremiumPrices prices : PremiumPrices.values()) {
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
            InventorySetter.setPremiumShopInventory(player, playerProfile.getInventoryProfile().getPremiumShopInventory());
            soundManager.playSound(XSound.ENTITY_PLAYER_LEVELUP);
            break;
        }
    }
}
