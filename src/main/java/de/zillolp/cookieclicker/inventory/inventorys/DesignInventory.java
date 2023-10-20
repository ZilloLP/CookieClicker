package de.zillolp.cookieclicker.inventory.inventorys;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.Design;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DesignInventory extends CustomInventory {
    private final ItemStack backItem;

    public DesignInventory(CookieClicker plugin, Player player, String title, int size) {
        super(plugin, player, title, size);
        this.backItem = new ItemBuilder(Material.PLAYER_HEAD, plugin.getLanguageConfig().getTranslatedLanguage("BACK"), "bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build();
    }

    @Override
    public void create() {
        design();
        PlayerProfile playerProfile = plugin.getCookieClickerManager().getPlayerProfiles().get(player.getUniqueId());
        inventory.setItem(27, backItem);
        for (Design design : Design.values()) {
            ItemStack item = new ItemBuilder(design.getType(), design.getName()).build();
            if (design == playerProfile.getDesign()) {
                item = new ItemBuilder(design.getType(), design.getSelectedName(), true).build();
            }
            inventory.setItem(design.getSlot(), item);
        }
    }

    @Override
    public void update() {

    }
}
