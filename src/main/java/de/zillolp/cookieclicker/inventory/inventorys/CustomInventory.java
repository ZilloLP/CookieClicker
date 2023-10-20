package de.zillolp.cookieclicker.inventory.inventorys;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public abstract class CustomInventory {
    protected Player player;

    protected CookieClicker plugin;
    protected Inventory inventory;
    protected String title;
    protected int size;

    public CustomInventory(CookieClicker plugin, Player player, String title, int size) {
        this.plugin = plugin;
        this.player = player;
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size, title);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                create();
            }
        }, 4);
    }


    public void openInventory(Player player) {
        if (inventory == null) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public abstract void create();

    public abstract void update();

    public void design() {
        PlayerProfile playerProfile = plugin.getCookieClickerManager().getPlayerProfiles().get(player.getUniqueId());
        if (playerProfile == null) {
            return;
        }
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            ItemStack item = inventory.getItem(slot);
            if (item == null) {
                inventory.setItem(slot, new ItemBuilder(playerProfile.getDesign().getDesignType(), languageConfig.getTranslatedLanguage("DESIGN_GLASS_NAME")).build());
                return;
            }
            Material type = item.getType();
            if (type == Material.AIR || (!(type.name().contains("_PANE")))) {
                return;
            }
            inventory.setItem(slot, new ItemBuilder(playerProfile.getDesign().getDesignType(), languageConfig.getTranslatedLanguage("DESIGN_GLASS_NAME")).build());
        });
    }
}
