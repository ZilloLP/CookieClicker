package de.zillolp.cookieclicker.inventory.inventorys;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HomeInventory extends CustomInventory {
    private final ItemStack shopItem;
    private final ItemStack designItem;

    public HomeInventory(CookieClicker plugin, Player player, String title, int size) {
        super(plugin, player, title, size);
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        this.shopItem = new ItemBuilder(Material.PINK_TERRACOTTA, languageConfig.getTranslatedLanguage("SHOP")).build();
        this.designItem = new ItemBuilder(Material.COOKIE, languageConfig.getTranslatedLanguage("DESIGN")).build();

    }

    @Override
    public void create() {
        design();
        inventory.setItem(24, shopItem);
        inventory.setItem(31, designItem);
    }

    @Override
    public void update() {
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        PlayerProfile playerProfile = plugin.getCookieClickerManager().getPlayerProfiles().get(player.getUniqueId());
        inventory.setItem(20, new ItemBuilder(Material.LIME_TERRACOTTA, languageConfig.getReplacedLanguage("PER_CLICK", playerProfile)).build());
        inventory.setItem(13, new ItemBuilder(Material.BROWN_TERRACOTTA, languageConfig.getReplacedLanguage("YOUR_COOKIES", playerProfile)).build());
    }

}
