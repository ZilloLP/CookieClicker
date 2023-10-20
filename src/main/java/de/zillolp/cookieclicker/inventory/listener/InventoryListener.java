package de.zillolp.cookieclicker.inventory.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.InventoryType;
import de.zillolp.cookieclicker.inventory.InventoryManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public abstract class InventoryListener implements Listener {
    protected final CookieClicker plugin;
    protected final InventoryManager inventoryManager;
    protected final InventoryType inventoryType;

    public InventoryListener(CookieClicker plugin, InventoryManager inventoryManager, InventoryType inventoryType) {
        this.plugin = plugin;
        this.inventoryManager = inventoryManager;
        this.inventoryType = inventoryType;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }
        if (!(event.getView().getTitle().equalsIgnoreCase(inventoryType.getTitle()))) {
            return;
        }
        UUID uuid = event.getWhoClicked().getUniqueId();
        if (clickedInventory != inventoryManager.getCustomInventories().get(uuid).get(inventoryType).getInventory()) {
            return;
        }
        event.setCancelled(true);
        if (inventoryManager.hasCoolDown(uuid)) {
            return;
        }
        inventoryManager.updateCoolDown(uuid);
        ItemMeta itemMeta = currentItem.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        onClick(event);
    }

    public abstract void onClick(InventoryClickEvent event);
}
