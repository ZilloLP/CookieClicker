package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.InventoryType;
import de.zillolp.cookieclicker.enums.SoundType;
import de.zillolp.cookieclicker.enums.StatsWallType;
import de.zillolp.cookieclicker.events.ClickerInteractEvent;
import de.zillolp.cookieclicker.manager.CookieClickerManager;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.protections.ProtectionManager;
import de.zillolp.cookieclicker.statswall.statswalls.TimeStatsWall;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class ClickerClickListener implements Listener {
    private final CookieClicker plugin;
    private final LanguageConfig languageConfig;

    public ClickerClickListener(CookieClicker plugin) {
        this.plugin = plugin;
        this.languageConfig = plugin.getLanguageConfig();
    }

    @EventHandler
    public void onClickerInteractEvent(ClickerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        CookieClickerManager cookieClickerManager = plugin.getCookieClickerManager();
        PlayerProfile playerProfile = cookieClickerManager.getPlayerProfiles().get(uuid);
        if (playerProfile == null) {
            return;
        }
        SoundManager soundManager = plugin.getSoundManager();
        switch (event.getInteractType()) {
            case ATTACK:
            case ADVENTURE_ATTACK:
                ProtectionManager protectionManager = plugin.getProtectionManager();
                protectionManager.addCPS(uuid);
                if (protectionManager.isAFK(uuid)) {
                    soundManager.playSound(player, SoundType.CLICK_DENY);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(languageConfig.getReplacedLanguage("AFK_MESSAGE", playerProfile)));
                    break;
                }
                if (protectionManager.isOverCPS(uuid)) {
                    soundManager.playSound(player, SoundType.CLICK_DENY);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(languageConfig.getReplacedLanguage("MAX_CPS", playerProfile)));
                    break;
                }
                addStats(player, playerProfile);
                soundManager.playSound(player, SoundType.CLICK_ALLOW);
                break;
            case INTERACT:
                plugin.getInventoryManager().openInventory(InventoryType.HOME, player);
                soundManager.playSound(player, SoundType.OPEN_INVENTORY);
                break;
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getHand() != EquipmentSlot.HAND || clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            return;
        }
        if (!(plugin.getCookieClickerManager().getClickerLocations().contains(clickedBlock.getLocation()))) {
            return;
        }
        event.setCancelled(true);
    }

    private void addStats(Player player, PlayerProfile playerProfile) {
        String name = player.getName();
        long perClick = playerProfile.getPerClick();
        playerProfile.addCookies(perClick);
        playerProfile.addClickerClicks(1);
        TimeStatsWall timeStatsWall = (TimeStatsWall) StatsWallType.TIME.getStatsWall();
        switch (plugin.getStatsWallManager().getRankType()) {
            case "cookies":
                timeStatsWall.addCachedData(name, perClick);
                break;
            case "clickerclicks":
                timeStatsWall.addCachedData(name, 1);
                break;
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(languageConfig.getReplacedLanguage("CLICK_MESSAGE", playerProfile)));
    }
}
