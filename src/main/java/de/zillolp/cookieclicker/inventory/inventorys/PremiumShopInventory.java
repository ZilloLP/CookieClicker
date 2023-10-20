package de.zillolp.cookieclicker.inventory.inventorys;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.Price;
import de.zillolp.cookieclicker.prices.PremiumPrice;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class PremiumShopInventory extends CustomInventory {
    private final ItemStack backItem;

    public PremiumShopInventory(CookieClicker plugin, Player player, String title, int size) {
        super(plugin, player, title, size);
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        this.backItem = new ItemBuilder(Material.PLAYER_HEAD, languageConfig.getTranslatedLanguage("BACK"), "bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build();
    }

    @Override
    public void create() {
        design();
        inventory.setItem(27, backItem);
    }

    @Override
    public void update() {
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        UUID uuid = player.getUniqueId();
        PlayerProfile playerProfile = plugin.getCookieClickerManager().getPlayerProfiles().get(uuid);
        for (Map.Entry<Price, PremiumPrice> priceEntry : plugin.getPriceManager().getPremiumPrices().entrySet()) {
            Price price = priceEntry.getKey();
            PremiumPrice premiumPrice = priceEntry.getValue();
            Material type = premiumPrice.getType();
            long costs = playerProfile.getPrice(price);
            long cookies = playerProfile.getCookies();
            String[] lore = new String[]{"", languageConfig.getPriceLanguage("PRICE_INFO", playerProfile, price)};
            if (cookies >= costs) {
                inventory.setItem(premiumPrice.getSlot(), new ItemBuilder(type, languageConfig.getPriceLanguage("PRICE_BUYABLE", playerProfile, price), lore, ItemFlag.values()).build());
                continue;
            }
            inventory.setItem(premiumPrice.getSlot(), new ItemBuilder(type, languageConfig.getPriceLanguage("PRICE_NOT_BUYABLE", playerProfile, price), lore, ItemFlag.values()).build());
        }
        inventory.setItem(2, new ItemBuilder(Material.LIME_TERRACOTTA, languageConfig.getReplacedLanguage("PER_CLICK", playerProfile)).build());
        inventory.setItem(6, new ItemBuilder(Material.BROWN_TERRACOTTA, languageConfig.getReplacedLanguage("YOUR_COOKIES", playerProfile)).build());
    }

}
