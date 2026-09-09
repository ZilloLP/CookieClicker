package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.clickerevents.ClickerEvent;
import de.zillolp.cookieclicker.clickerevents.CookieExplosionEvent;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.enums.ClickerEventType;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.holograms.MovingHologram;
import de.zillolp.cookieclicker.manager.ClickerEventManager;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.manager.HologramManager;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CookieExplosionListener implements Listener {
    private final CookieClicker plugin;
    private final PluginConfig pluginConfig;
    private final LanguageConfig languageConfig;
    private final ClickerPlayerManager clickerPlayerManager;
    private final HologramManager hologramManager;
    private final ClickerEventManager clickerEventManager;

    public CookieExplosionListener(CookieClicker plugin) {
        this.plugin = plugin;
        pluginConfig = plugin.getPluginConfig();
        languageConfig = plugin.getLanguageConfig();
        clickerPlayerManager = plugin.getClickerPlayerManager();
        hologramManager = plugin.getHologramManager();
        clickerEventManager = plugin.getClickerEventManager();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        for (Map.Entry<UUID, HashMap<ClickerEventType, ClickerEvent>> activeClickerEvents : clickerEventManager.getActiveEvents().entrySet()) {
            if (activeClickerEvents.getKey().equals(playerUuid)) {
                continue;
            }
            HashMap<ClickerEventType, ClickerEvent> clickerEvents = activeClickerEvents.getValue();
            if (clickerEvents == null) {
                continue;
            }
            ClickerEvent clickerEvent = clickerEvents.get(ClickerEventType.COOKIE_EXPLOSION);
            if (clickerEvent == null || clickerEvent.getTime() <= 0) {
                continue;
            }
            CookieExplosionEvent cookieExplosionEvent = (CookieExplosionEvent) clickerEvent;
            ArrayList<Item> items = cookieExplosionEvent.getItems();
            if (items.isEmpty()) {
                continue;
            }
            int[] entityIds = items.stream().mapToInt(Item::getEntityId).toArray();
            plugin.getNmsBridge().sendEntityRemovePacket(player, entityIds);
        }
    }

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        for (Map.Entry<UUID, HashMap<ClickerEventType, ClickerEvent>> activeClickerEvents : clickerEventManager.getActiveEvents().entrySet()) {
            if (activeClickerEvents.getKey().equals(playerUuid)) {
                continue;
            }
            HashMap<ClickerEventType, ClickerEvent> clickerEvents = activeClickerEvents.getValue();
            if (clickerEvents == null) {
                continue;
            }
            ClickerEvent clickerEvent = clickerEvents.get(ClickerEventType.COOKIE_EXPLOSION);
            if (clickerEvent == null || clickerEvent.getTime() <= 0) {
                continue;
            }
            CookieExplosionEvent cookieExplosionEvent = (CookieExplosionEvent) clickerEvent;
            ArrayList<Item> items = cookieExplosionEvent.getItems();
            if (items.isEmpty()) {
                continue;
            }
            int[] entityIds = items.stream()
                    .filter(item -> item.getWorld().equals(player.getWorld()))
                    .mapToInt(Item::getEntityId)
                    .toArray();
            if (entityIds.length > 0) {
                plugin.getNmsBridge().sendEntityRemovePacket(player, entityIds);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Item item = event.getItem();

        for (Map.Entry<UUID, HashMap<ClickerEventType, ClickerEvent>> activeClickerEvents : clickerEventManager.getActiveEvents().entrySet()) {
            HashMap<ClickerEventType, ClickerEvent> clickerEvents = activeClickerEvents.getValue();
            if (clickerEvents == null) {
                continue;
            }

            ClickerEvent clickerEvent = clickerEvents.get(ClickerEventType.COOKIE_EXPLOSION);
            if (clickerEvent == null || clickerEvent.getTime() <= 0) {
                continue;
            }

            CookieExplosionEvent cookieExplosionEvent = (CookieExplosionEvent) clickerEvent;
            ArrayList<Item> items = cookieExplosionEvent.getItems();
            if (!items.contains(item)) {
                continue;
            }

            event.setCancelled(true);

            if (!activeClickerEvents.getKey().equals(uuid)) {
                return;
            }

            plugin.getNmsBridge().sendTakeItemPacket(player, item.getEntityId(), player.getEntityId(), item.getItemStack().getAmount());
            item.remove();
            items.remove(item);

            ClickerStatsProfile clickerStatsProfile = clickerPlayerManager.getStatsProfile(uuid);
            if (clickerStatsProfile == null) {
                return;
            }

            long cookiesPerCookie = clickerStatsProfile.getPerClick() * pluginConfig.getCookiesPerCookieMultiplier();
            clickerStatsProfile.addCookies(cookiesPerCookie);
            hologramManager.spawnHologram(player, new MovingHologram(plugin, languageConfig.getReplacedLanguage(PluginLanguage.COLLECTED_COOKIE, uuid)
                    .replace("%collectedcookies%", languageConfig.formatNumber(cookiesPerCookie)), 20, 0.10), item.getLocation());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(languageConfig.getReplacedLanguage(PluginLanguage.COOKIE_COLLECT_MESSAGE, uuid)
                    .replace("%collectedcookies%", languageConfig.formatNumber(cookiesPerCookie))));
            return;
        }
    }

    @EventHandler
    public void onItemMergeEvent(ItemMergeEvent event) {
        Item item = event.getEntity();
        for (HashMap<ClickerEventType, ClickerEvent> clickerEvents : clickerEventManager.getActiveEvents().values()) {
            if (clickerEvents == null) {
                continue;
            }

            ClickerEvent clickerEvent = clickerEvents.get(ClickerEventType.COOKIE_EXPLOSION);
            if (clickerEvent == null || clickerEvent.getTime() <= 0) {
                continue;
            }

            CookieExplosionEvent cookieExplosionEvent = (CookieExplosionEvent) clickerEvent;
            if (cookieExplosionEvent.getItems().contains(item)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
