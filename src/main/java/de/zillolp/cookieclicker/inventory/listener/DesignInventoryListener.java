package de.zillolp.cookieclicker.inventory.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.Design;
import de.zillolp.cookieclicker.enums.InventoryType;
import de.zillolp.cookieclicker.enums.SoundType;
import de.zillolp.cookieclicker.inventory.InventoryManager;
import de.zillolp.cookieclicker.inventory.inventorys.CustomInventory;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DesignInventoryListener extends InventoryListener {

    public DesignInventoryListener(CookieClicker plugin, InventoryManager inventoryManager, InventoryType inventoryType) {
        super(plugin, inventoryManager, inventoryType);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        SoundManager soundManager = plugin.getSoundManager();
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        Material type = currentItem.getType();
        String displayName = currentItem.getItemMeta().getDisplayName();
        if (type == Material.PLAYER_HEAD && displayName.equalsIgnoreCase(plugin.getLanguageConfig().getTranslatedLanguage("BACK"))) {
            inventoryManager.openInventory(InventoryType.HOME, player);
            soundManager.playSound(player, SoundType.BACK);
            return;
        }
        UUID uuid = player.getUniqueId();
        PlayerProfile playerProfile = plugin.getCookieClickerManager().getPlayerProfiles().get(uuid);
        Design currentDesign = playerProfile.getDesign();
        Inventory clickedInventory = event.getClickedInventory();
        for (Design design : Design.values()) {
            Material designType = design.getType();
            if (type != designType || (!(displayName.equalsIgnoreCase(design.getName())))) {
                continue;
            }
            if (currentDesign == design) {
                break;
            }
            clickedInventory.setItem(currentDesign.getSlot(), new ItemBuilder(currentDesign.getType(), currentDesign.getName()).build());
            clickedInventory.setItem(design.getSlot(), new ItemBuilder(designType, design.getSelectedName(), true).build());
            playerProfile.setDesign(design);
            soundManager.playSound(player, SoundType.SELECT_DESIGN);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    for (CustomInventory customInventory : inventoryManager.getCustomInventories().get(uuid).values()) {
                        customInventory.design();
                    }
                }
            });
            break;
        }
    }
}
