package de.zillolp.cookieclicker.inventory.inventorys;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.Price;
import de.zillolp.cookieclicker.prices.NormalPrice;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class ShopInventory extends CustomInventory {
    private final ItemStack premiumPageItem;
    private final ItemStack backItem;

    public ShopInventory(CookieClicker plugin, Player player, String title, int size) {
        super(plugin, player, title, size);
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        this.premiumPageItem = new ItemBuilder(Material.PLAYER_HEAD, languageConfig.getTranslatedLanguage("PREMIUM_PAGE"), "19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build();
        this.backItem = new ItemBuilder(Material.PLAYER_HEAD, languageConfig.getTranslatedLanguage("BACK"), "bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build();
    }

    @Override
    public void create() {
        design();
        inventory.setItem(35, premiumPageItem);
        inventory.setItem(27, backItem);
    }

    @Override
    public void update() {
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        UUID uuid = player.getUniqueId();
        PlayerProfile playerProfile = plugin.getCookieClickerManager().getPlayerProfiles().get(uuid);
        for (Map.Entry<Price, NormalPrice> priceEntry : plugin.getPriceManager().getPrices().entrySet()) {
            Price price = priceEntry.getKey();
            NormalPrice normalPrice = priceEntry.getValue();
            Material type = normalPrice.getType();
            long costs = playerProfile.getPrice(price);
            long cookies = playerProfile.getCookies();
            String[] lore = new String[]{"", languageConfig.getPriceLanguage("PRICE_INFO", playerProfile, price)};
            if (cookies >= costs) {
                inventory.setItem(normalPrice.getSlot(), new ItemBuilder(type, languageConfig.getPriceLanguage("PRICE_BUYABLE", playerProfile, price), lore, ItemFlag.values()).build());
                continue;
            }
            inventory.setItem(normalPrice.getSlot(), new ItemBuilder(type, languageConfig.getPriceLanguage("PRICE_NOT_BUYABLE", playerProfile, price), lore, ItemFlag.values()).build());
        }
        inventory.setItem(2, new ItemBuilder(Material.LIME_TERRACOTTA, languageConfig.getReplacedLanguage("PER_CLICK", playerProfile)).build());
        inventory.setItem(6, new ItemBuilder(Material.BROWN_TERRACOTTA, languageConfig.getReplacedLanguage("YOUR_COOKIES", playerProfile)).build());
    }

}
