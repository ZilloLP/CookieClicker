package de.zillolp.cookieclicker.utils;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.enums.Designs;
import de.zillolp.cookieclicker.enums.PremiumPrices;
import de.zillolp.cookieclicker.enums.Prices;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.xclasses.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public class InventorySetter {
    private static final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    public static void setDesign(Player player, Inventory inventory) {
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        IntStream.range(0, inventory.getSize()).forEach(i -> inventory.setItem(i, new ItemBuilder(playerProfile.getDesign().getDesignType(), LanguageTools.getLanguage("DESIGN_GLASS_NAME")).build()));
    }

    public static void setHomeInventory(Player player, Inventory inventory) {
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        setDesign(player, inventory);
        inventory.setItem(20, new ItemBuilder(XMaterial.LIME_TERRACOTTA, LanguageTools.getLanguageReplaced("PER_CLICK", "%perClick%", playerProfile.getPerClick())).build());
        inventory.setItem(24, new ItemBuilder(XMaterial.PINK_TERRACOTTA, LanguageTools.getLanguage("SHOP")).build());
        inventory.setItem(31, new ItemBuilder(XMaterial.COOKIE, LanguageTools.getLanguage("DESIGN")).build());
        inventory.setItem(13, new ItemBuilder(XMaterial.BROWN_TERRACOTTA, LanguageTools.getLanguageReplaced("YOUR_COOKIES", "%cookies%", playerProfile.getCookies())).build());
    }

    public static void setShopInventory(Player player, Inventory inventory) {
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        setDesign(player, inventory);
        inventory.setItem(2, new ItemBuilder(XMaterial.BROWN_TERRACOTTA, LanguageTools.getLanguageReplaced("YOUR_COOKIES", "%cookies%", playerProfile.getCookies())).build());
        inventory.setItem(6, new ItemBuilder(XMaterial.LIME_TERRACOTTA, LanguageTools.getLanguageReplaced("PER_CLICK", "%perClick%", playerProfile.getPerClick())).build());
        for (Prices prices : Prices.values()) {
            long price = playerProfile.getPrice(prices.getNumber());
            XMaterial type = prices.getType();
            String[] lore = new String[]{"", LanguageTools.getLanguageReplaced("PRICE_INFO", "%addclick%", prices.getAddClick())};
            if (playerProfile.getCookies() >= price) {
                inventory.setItem(19 + prices.getNumber(), new ItemBuilder(type, LanguageTools.getLanguageReplaced("PRICE_BUYABLE", "%price%", price), lore, ItemFlag.values()).build());
                continue;
            }
            inventory.setItem(19 + prices.getNumber(), new ItemBuilder(type, LanguageTools.getLanguageReplaced("PRICE_NOT_BUYABLE", "%price%", price), lore, ItemFlag.values()).build());
        }
        inventory.setItem(35, new ItemBuilder(XMaterial.PLAYER_HEAD, LanguageTools.getLanguage("PREMIUM_PAGE"), "19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build());
        inventory.setItem(27, new ItemBuilder(XMaterial.PLAYER_HEAD, LanguageTools.getLanguage("BACK"), "bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build());
    }

    public static void setPremiumShopInventory(Player player, Inventory inventory) {
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        setDesign(player, inventory);
        inventory.setItem(2, new ItemBuilder(XMaterial.BROWN_TERRACOTTA, LanguageTools.getLanguageReplaced("YOUR_COOKIES", "%cookies%", playerProfile.getCookies())).build());
        inventory.setItem(6, new ItemBuilder(XMaterial.LIME_TERRACOTTA, LanguageTools.getLanguageReplaced("PER_CLICK", "%perClick%", playerProfile.getPerClick())).build());
        for (PremiumPrices prices : PremiumPrices.values()) {
            long price = playerProfile.getPrice(prices.getNumber());
            XMaterial type = prices.getType();
            String[] lore = new String[]{"", LanguageTools.getLanguageReplaced("PRICE_INFO", "%addclick%", prices.getAddClick())};
            if (playerProfile.getCookies() >= price) {
                inventory.setItem(12 + prices.getNumber(), new ItemBuilder(type, LanguageTools.getLanguageReplaced("PRICE_BUYABLE", "%price%", price), lore, ItemFlag.values()).build());
                continue;
            }
            inventory.setItem(12 + prices.getNumber(), new ItemBuilder(type, LanguageTools.getLanguageReplaced("PRICE_NOT_BUYABLE", "%price%", price), lore, ItemFlag.values()).build());
        }
        inventory.setItem(27, new ItemBuilder(XMaterial.PLAYER_HEAD, LanguageTools.getLanguage("BACK"), "bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build());
    }

    public static void setDesignInventory(Player player, Inventory inventory) {
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        setDesign(player, inventory);
        int slot = 10;
        for (Designs design : Designs.values()) {
            ItemStack designItem = new ItemBuilder(design.getType(), design.getName()).build();
            if (design == playerProfile.getDesign()) {
                designItem = new ItemBuilder(design.getType(), design.getSelectedName(), true).build();
            }
            if (slot >= 17) {
                inventory.setItem(slot + 1, designItem);
                slot++;
                continue;
            }
            inventory.setItem(slot, designItem);
            slot++;
        }
        inventory.setItem(27, new ItemBuilder(XMaterial.PLAYER_HEAD, LanguageTools.getLanguage("BACK"), "bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build());
    }

}
