package de.zillolp.cookieclicker.inventory.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.InventoryType;
import de.zillolp.cookieclicker.enums.Price;
import de.zillolp.cookieclicker.enums.SoundType;
import de.zillolp.cookieclicker.enums.StatsWallType;
import de.zillolp.cookieclicker.inventory.InventoryManager;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.prices.NormalPrice;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.statswall.statswalls.TimeStatsWall;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class ShopInventoryListener extends InventoryListener {
    private final LanguageConfig languageConfig;

    public ShopInventoryListener(CookieClicker plugin, InventoryManager inventoryManager, InventoryType inventoryType) {
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
        if (type == Material.PLAYER_HEAD && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage("BACK"))) {
            inventoryManager.openInventory(InventoryType.HOME, player);
            soundManager.playSound(player, SoundType.BACK);
            return;
        } else if (type == Material.PLAYER_HEAD && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage("PREMIUM_PAGE"))) {
            if (!(plugin.getPermissionsConfig().hasPermission(player, "PREMIUM_PERMISSION"))) {
                soundManager.playSound(player, SoundType.PREMIUM_DENY);
                return;
            }
            inventoryManager.openInventory(InventoryType.PREMIUM_SHOP, player);
            soundManager.playSound(player, SoundType.PREMIUM_ALLOW);
            return;
        }
        int clickedSlot = event.getSlot();
        UUID uuid = player.getUniqueId();
        PlayerProfile playerProfile = plugin.getCookieClickerManager().getPlayerProfiles().get(uuid);
        for (Map.Entry<Price, NormalPrice> priceEntry : plugin.getPriceManager().getPrices().entrySet()) {
            Price price = priceEntry.getKey();
            NormalPrice normalPrice = priceEntry.getValue();
            if (clickedSlot != normalPrice.getSlot() || type != normalPrice.getType()) {
                continue;
            }
            long priceAmount = playerProfile.getPrice(price);
            if (playerProfile.getCookies() < priceAmount && displayName.equalsIgnoreCase(languageConfig.getPriceLanguage("PRICE_NOT_BUYABLE", playerProfile, price))) {
                soundManager.playSound(player, SoundType.BUY_DENY);
                break;
            }
            if (!(displayName.equalsIgnoreCase(languageConfig.getPriceLanguage("PRICE_BUYABLE", playerProfile, price)))) {
                continue;
            }
            playerProfile.removeCookies(priceAmount);
            playerProfile.addPrice(price);
            playerProfile.addPerClick(normalPrice.getAddClick());
            soundManager.playSound(player, SoundType.BUY_ALLOW);
            if (plugin.getStatsWallManager().getRankType().equalsIgnoreCase("perclick")) {
                TimeStatsWall timeStatsWall = (TimeStatsWall) StatsWallType.TIME.getStatsWall();
                timeStatsWall.addCachedData(playerProfile.getName(), normalPrice.getAddClick());
            }
            inventoryManager.getCustomInventories().get(uuid).get(inventoryType).update();
            break;
        }
    }
}
