package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.enums.Designs;
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

public class DesignInventoryListener implements Listener {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }
        String title = event.getView().getTitle();
        if (!(title.equalsIgnoreCase(LanguageTools.getLanguage("DESIGN_TITLE")))) {
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
            Inventory homeInventory = inventoryProfile.getHomeInventory();
            if (homeInventory == null) {
                homeInventory = inventoryProfile.setHomeInventory(Bukkit.createInventory(null, 5 * 9, LanguageTools.getLanguage("HOME_TITLE")));
            }
            InventorySetter.setHomeInventory(player, homeInventory);
            player.openInventory(homeInventory);
            soundManager.playSound(XSound.BLOCK_NOTE_BLOCK_SNARE);
            return;
        }
        for (Designs design : Designs.values()) {
            if (!(type == design.getType().parseMaterial() && design.getName().contains(displayName))) {
                continue;
            }
            playerProfile.setDesigns(design);
            InventorySetter.setDesignInventory(player, playerProfile.getInventoryProfile().getDesignInventory());
            soundManager.playSound(XSound.BLOCK_NOTE_BLOCK_PLING);
            break;
        }
    }
}
