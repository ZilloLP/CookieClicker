package de.zillolp.cookieclicker.handler;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.clickerevents.CookieExplosionEvent;
import de.zillolp.cookieclicker.clickerevents.GoldenCookieEvent;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.customevents.CookieClickerInteractEvent;
import de.zillolp.cookieclicker.custominventories.CustomInventory;
import de.zillolp.cookieclicker.enums.*;
import de.zillolp.cookieclicker.manager.ClickerEventManager;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.manager.DesignManager;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.profiles.ClickerGameProfile;
import de.zillolp.cookieclicker.profiles.ClickerInventoryProfile;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

public class ClickerHandler {
    private final CookieClicker plugin;
    private final PluginConfig pluginConfig;
    private final LanguageConfig languageConfig;
    private final DesignManager designManager;
    private final SoundManager soundManager;
    private final ClickerPlayerManager clickerPlayerManager;
    private final BukkitScheduler bukkitScheduler;
    private final Random random = new Random();

    public ClickerHandler(CookieClicker plugin) {
        this.plugin = plugin;
        pluginConfig = plugin.getPluginConfig();
        languageConfig = plugin.getLanguageConfig();
        designManager = plugin.getDesignManager();
        soundManager = plugin.getSoundManager();
        clickerPlayerManager = plugin.getClickerPlayerManager();
        bukkitScheduler = plugin.getServer().getScheduler();
    }

    public void handleClickerInteract(CookieClickerInteractEvent event) {
        bukkitScheduler.runTaskAsynchronously(plugin, () -> {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            ClickerGameProfile clickerGameProfile = clickerPlayerManager.getGameProfile(uuid);
            ClickerStatsProfile clickerStatsProfile = clickerPlayerManager.getStatsProfile(uuid);
            if (!(clickerGameProfile.isFirstClicked())) {
                clickerGameProfile.setFirstClicked(true);
                designManager.sendClickerBlockDesign(player);
            }
            switch (event.getInteractType()) {
                case LEFT_CLICK:
                case ADVENTURE_LEFT_CLICK:
                    Player.Spigot spigotPlayer = player.spigot();
                    clickerGameProfile.addPlayerClicksPerSecond(1);
                    if (clickerGameProfile.isUnderLastPlayerMove(pluginConfig.getAFKCooldownSeconds())) {
                        soundManager.playSound(player, SoundType.CLICK_DENY);
                        spigotPlayer.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(languageConfig.getReplacedLanguage(PluginLanguage.AFK_MESSAGE, uuid)));
                        break;
                    }
                    if (clickerGameProfile.isOverCPS(pluginConfig.getMaximumClicksPerSecond())) {
                        soundManager.playSound(player, SoundType.CLICK_DENY);
                        spigotPlayer.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(languageConfig.getReplacedLanguage(PluginLanguage.MAX_CPS, uuid)));
                        break;
                    }
                    long particleDesign = clickerStatsProfile.getParticleDesign();
                    if (clickerGameProfile.isOverLastParticleEffect() && particleDesign > 0) {
                        clickerGameProfile.updateLastParticleEffect(pluginConfig.getCustomParticleEffectType(ShopType.HIT_PARTICLE_DESIGN, particleDesign).getDelay());
                        designManager.sendParticleEffect(player, event.getLocation(), particleDesign);
                    }

                    clickerStatsProfile.addClickerClicks(1);
                    soundManager.playSound(player, SoundType.CLICK_ALLOW);

                    ClickerEventManager clickerEventManager = plugin.getClickerEventManager();
                    if (clickerEventManager.isActive(uuid, ClickerEventType.GOLDEN_COOKIE)) {
                        clickerStatsProfile.addCookies(clickerStatsProfile.getPerClick() * pluginConfig.getPerClickMultiplier());
                        break;
                    }

                    if (random.nextInt(pluginConfig.getGoldenCookieRange()) <= 1) {
                        player.sendTitle(languageConfig.getReplacedLanguage(PluginLanguage.GOLDEN_COOKIE_TITLE_HEADER, uuid),
                                languageConfig.getReplacedLanguage(PluginLanguage.GOLDEN_COOKIE_TITLE_FOOTER, uuid), 20, 40, 20);
                        clickerEventManager.activateEvent(uuid, new GoldenCookieEvent(player, ClickerEventType.GOLDEN_COOKIE, pluginConfig.getGoldenDurationSeconds(), event.getLocation().getBlock()));
                        soundManager.playSound(player, SoundType.GOLDEN_COOKIE);
                    } else if (random.nextInt(pluginConfig.getCookieExplosionRange()) <= 1 && (!(clickerEventManager.isActive(uuid, ClickerEventType.COOKIE_EXPLOSION)))) {
                        player.sendTitle(languageConfig.getReplacedLanguage(PluginLanguage.COOKIE_EXPLOSION_TITLE_HEADER, uuid),
                                languageConfig.getReplacedLanguage(PluginLanguage.COOKIE_EXPLOSION_TITLE_FOOTER, uuid), 20, 40, 20);
                        int explosionDuration = pluginConfig.getExplosionDurationSeconds();
                        CookieExplosionEvent cookieExplosionEvent = new CookieExplosionEvent(plugin, player, ClickerEventType.COOKIE_EXPLOSION, explosionDuration);
                        clickerEventManager.activateEvent(uuid, cookieExplosionEvent);
                        Location location = event.getLocation().clone().add(0.5, 1, 0.5);
                        bukkitScheduler.runTask(plugin, () -> {
                            for (int number = 0; number < pluginConfig.getCookieAmount(); number++) {
                                Item item = player.getWorld().dropItemNaturally(location, plugin.getItemBuilder().build(Material.COOKIE, null, 1));
                                item.setInvulnerable(true);
                                item.setPickupDelay(explosionDuration);
                                item.setVelocity(new Vector(random.nextDouble(-0.2, 0.2), 1, random.nextDouble(-0.2, 0.2)));
                                cookieExplosionEvent.addGlowing(item);
                                cookieExplosionEvent.getItems().add(item);
                            }
                            int[] entityIds = cookieExplosionEvent.getItems().stream().mapToInt(Item::getEntityId).toArray();
                            if (entityIds.length > 0) {
                                for (Player player1 : Bukkit.getOnlinePlayers()) {
                                    if (player != player1) {
                                        plugin.getNmsBridge().sendEntityRemovePacket(player1, entityIds);
                                    }
                                }
                            }
                        });
                        soundManager.playSound(player, SoundType.COOKIE_EXPLOSION);
                    }
                    clickerStatsProfile.addCookies(clickerStatsProfile.getPerClick());
                    spigotPlayer.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(languageConfig.getReplacedLanguage(PluginLanguage.CLICK_MESSAGE, uuid)));
                    break;
                case HAND_RIGHT_CLICK:
                    if (event.isBlockPlaced()) {
                        designManager.sendClickerBlockDesign(player);
                    }
                    ClickerInventoryProfile clickerInventoryProfile = clickerPlayerManager.getInventoryProfile(player);
                    CustomInventory customInventory = clickerInventoryProfile.getCustomInventories().get(CustomInventoryType.HOME);
                    if (customInventory == null) {
                        break;
                    }
                    soundManager.playSound(player, SoundType.OPEN_INVENTORY);
                    bukkitScheduler.runTask(plugin, () -> customInventory.openInventory(player));
                    break;
            }
        });
    }
}
